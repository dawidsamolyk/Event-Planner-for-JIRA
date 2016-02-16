package edu.uz.jira.event.planner.project;

import com.atlassian.jira.JiraException;
import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.blueprint.api.*;
import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.sal.api.message.I18nResolver;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.xml.importer.EventPlansImportExecutor;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.project.issue.fields.IssueFieldsConfigurator;
import edu.uz.jira.event.planner.workflow.WorkflowConfigurator;
import edu.uz.jira.event.planner.workflow.WorkflowConstants;
import edu.uz.jira.event.planner.workflow.WorkflowUtils;
import edu.uz.jira.event.planner.workflow.descriptor.WorkflowDescriptorsFactory;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Validates and configures all created Event Organization Plan Projects.
 */
public class EventOrganizationProjectHook implements AddProjectHook {
    public static final String REDIRECT_URL = "/secure/EventOrganizationPlanConfiguration.jspa";
    private static final String REDIRECT_URL_ARGUMENTS = "?project-key=%s";
    private final IssueFieldsConfigurator issueFieldsConfigurator;
    private final WorkflowConfigurator workflowConfigurator;
    private final WorkflowDescriptorsFactory workflowDescriptorsFactory;
    private final ProjectCategoryConfigurator projectCategoryConfigurator;
    private final WorkflowUtils utils;
    private final ActiveObjectsService activeObjectsService;
    private final I18nResolver i18nResolver;

    /**
     * Constructor.
     *
     * @param i18nResolver              Injected {@code I18nResolver} implementation.
     * @param workflowTransitionService Injected {@code WorkflowTransitionService} implementation.
     * @param activeObjectsService      Injected {@code ActiveObjectsService} implementation.
     * @throws NullArgumentException Thrown when any input argument is null.
     */
    public EventOrganizationProjectHook(@Nonnull final I18nResolver i18nResolver,
                                        @Nonnull final WorkflowTransitionService workflowTransitionService,
                                        @Nonnull final ActiveObjectsService activeObjectsService) throws NullArgumentException {
        workflowConfigurator = new WorkflowConfigurator(workflowTransitionService);
        workflowDescriptorsFactory = new WorkflowDescriptorsFactory();
        issueFieldsConfigurator = new IssueFieldsConfigurator(i18nResolver);
        projectCategoryConfigurator = new ProjectCategoryConfigurator(i18nResolver);
        utils = new WorkflowUtils();
        this.activeObjectsService = activeObjectsService;
        this.i18nResolver = i18nResolver;
    }

    /**
     * @param validateData Data to validate.
     * @return Response which indicates that validation has completed successfully or not.
     */
    @Override
    public ValidateResponse validate(@Nonnull final ValidateData validateData) {
        importPredefinedEventPlansIfRequired(i18nResolver, activeObjectsService);
        return ValidateResponse.create();
    }

    private void importPredefinedEventPlansIfRequired(@Nonnull I18nResolver i18nResolver, @Nonnull ActiveObjectsService activeObjectsService) {
        EventPlansImportExecutor importExecutor = new EventPlansImportExecutor(i18nResolver, activeObjectsService);
        importExecutor.startImport();
    }

    /**
     * @param configureData Data to configure.
     * @return Response after configuring data (eg. redirect to specified page).
     */
    @Override
    public ConfigureResponse configure(@Nonnull final ConfigureData configureData) {
        Project project = configureData.project();
        JiraWorkflow workflow = configureData.createdWorkflows().get(WorkflowConstants.EVENT_ORGANIZATION_WORKFLOW_KEY);

        configureProjectCategory(project);
        configureFieldLayout(project);

        try {
            configureWorkflow(workflow);

        } catch (JiraException e) {
            return ConfigureResponse.create().setRedirect(REDIRECT_URL);
        }

        return ConfigureResponse.create().setRedirect(REDIRECT_URL + String.format(REDIRECT_URL_ARGUMENTS, project.getKey()));
    }

    private void configureProjectCategory(@Nonnull final Project project) {
        ProjectCategory projectCategory = projectCategoryConfigurator.createProjectCategory();
        projectCategoryConfigurator.assign(projectCategory, project);
    }

    private void configureFieldLayout(@Nonnull final Project project) {
        EditableFieldLayout fieldLayout = issueFieldsConfigurator.getEventOrganizationFieldLayout();
        fieldLayout = issueFieldsConfigurator.storeAndReturnEventOrganizationFieldLayout(fieldLayout);

        FieldLayoutScheme fieldConfigurationScheme = issueFieldsConfigurator.createFieldConfigurationScheme(project, fieldLayout);
        issueFieldsConfigurator.storeFieldConfigurationScheme(project, fieldConfigurationScheme);
    }

    private void configureWorkflow(final JiraWorkflow workflow) throws JiraException {
        if (workflow != null) {
            ValidatorDescriptor validator = workflowDescriptorsFactory.createIssueDueDateValidatorDescriptor();
            workflowConfigurator.addToDraft(workflow, validator, WorkflowConstants.CREATE_WORKFLOW_ACTION_NAME);

            List<String> statusesWhichBlocks = utils.getStatusesFromCategory(workflow, WorkflowConstants.COMPLETE_STATUS_CATEGORY_NAME);
            ConditionDescriptor condition = workflowDescriptorsFactory.createSubTaskBlockingConditionDescriptor(statusesWhichBlocks);
            workflowConfigurator.addToDraft(workflow, condition, WorkflowConstants.DONE_STATUS_NAME);
            workflowConfigurator.addToDraft(workflow, condition, WorkflowConstants.RESOLVED_STATUS_NAME);

            workflowConfigurator.publishDraft(workflow);
        } else {
            throw new JiraException();
        }
    }
}
