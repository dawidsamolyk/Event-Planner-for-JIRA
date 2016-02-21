package edu.uz.jira.event.planner.project;

import com.atlassian.extras.common.log.Logger;
import com.atlassian.jira.JiraException;
import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.blueprint.api.*;
import com.atlassian.jira.exception.DataAccessException;
import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.sal.api.message.I18nResolver;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import edu.uz.jira.event.planner.database.xml.importer.EventPlansImportExecutor;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.project.issue.fields.IssueFieldsConfigurator;
import edu.uz.jira.event.planner.workflow.WorkflowConfigurator;
import edu.uz.jira.event.planner.workflow.WorkflowConstants;
import edu.uz.jira.event.planner.workflow.WorkflowUtils;
import edu.uz.jira.event.planner.workflow.descriptor.WorkflowDescriptorsFactory;
import org.ofbiz.core.entity.GenericEntityException;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Validates and configures Event Organization Plan project.
 */
public class EventOrganizationProjectHook implements AddProjectHook {
    public static final String REDIRECT_URL = "/secure/EventOrganizationPlanConfiguration.jspa";
    public static final String REDIRECT_URL_ARGUMENTS = "?project-key=%s";
    public static final String[] REQUIRED_TASK_FIELDS_IDS = new String[]{"duedate", "components"};
    private final Logger.Log log;
    private final IssueFieldsConfigurator issueFieldsConfigurator;
    private final WorkflowConfigurator workflowConfigurator;
    private final WorkflowDescriptorsFactory workflowDescriptorsFactory;
    private final ProjectCategoryConfigurator projectCategoryConfigurator;
    private final WorkflowUtils utils;
    private final EventPlansImportExecutor importExecutor;

    /**
     * Constructor.
     *
     * @param i18nResolver              Injected {@code I18nResolver} implementation.
     * @param workflowTransitionService Injected {@code WorkflowTransitionService} implementation.
     * @param importExecutor            Injected {@code EventPlansImportExecutor} implementation.
     * @throws NullArgumentException Thrown when one of input arguments is null.
     */
    public EventOrganizationProjectHook(@Nonnull final I18nResolver i18nResolver,
                                        @Nonnull final WorkflowTransitionService workflowTransitionService,
                                        @Nonnull final EventPlansImportExecutor importExecutor) throws NullArgumentException {
        workflowConfigurator = new WorkflowConfigurator(workflowTransitionService);
        workflowDescriptorsFactory = new WorkflowDescriptorsFactory();
        issueFieldsConfigurator = new IssueFieldsConfigurator(i18nResolver);
        projectCategoryConfigurator = new ProjectCategoryConfigurator(i18nResolver);
        utils = new WorkflowUtils();
        this.importExecutor = importExecutor;
        log = Logger.getInstance(this.getClass());
    }

    /**
     * @param validateData Data to validate.
     * @return Response which indicates that validation has completed successfully or not.
     */
    @Override
    public ValidateResponse validate(@Nonnull final ValidateData validateData) {
        if (!importExecutor.isDataImported()) {
            importExecutor.startImport();
        }
        return ValidateResponse.create();
    }

    /**
     * @param configureData Data to configure.
     * @return Response after configuring data (eg. redirect to specified page).
     */
    @Override
    public ConfigureResponse configure(@Nonnull final ConfigureData configureData) {
        ConfigureResponse responseForErrorState = ConfigureResponse.create().setRedirect(REDIRECT_URL);
        Project project;

        try {
            project = configureData.project();
            JiraWorkflow workflow = configureData.createdWorkflows().get(WorkflowConstants.EVENT_ORGANIZATION_WORKFLOW_KEY);

            configureProjectCategory(project);
            configureFieldLayout(project);
            configureWorkflow(workflow);

        } catch (JiraException e) {
            log.error(e.getMessage(), e);
            return responseForErrorState;
        } catch (DataAccessException e) {
            log.error(e.getMessage(), e);
            return responseForErrorState;
        } catch (GenericEntityException e) {
            log.error(e.getMessage(), e);
            return responseForErrorState;
        } catch (NullPointerException e) {
            log.error(e.getMessage(), e);
            return responseForErrorState;
        } catch (NoSuchElementException e) {
            log.error(e.getMessage(), e);
            return responseForErrorState;
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage(), e);
            return responseForErrorState;
        }

        return ConfigureResponse.create().setRedirect(REDIRECT_URL + String.format(REDIRECT_URL_ARGUMENTS, project.getKey()));
    }

    private void configureProjectCategory(@Nonnull final Project project) {
        ProjectCategory projectCategory = projectCategoryConfigurator.getProjectCategory();
        projectCategoryConfigurator.assign(projectCategory, project);
    }

    private void configureFieldLayout(@Nonnull final Project project) throws GenericEntityException {
        EditableFieldLayout fieldLayout = issueFieldsConfigurator.getFieldLayoutCopyWithRequiredFields(REQUIRED_TASK_FIELDS_IDS);
        fieldLayout = issueFieldsConfigurator.storeAndReturn(fieldLayout);

        FieldLayoutScheme fieldConfigurationScheme = issueFieldsConfigurator.createConfigurationScheme(project, fieldLayout);
        issueFieldsConfigurator.addSchemeAssociation(project, fieldConfigurationScheme);
    }

    private void configureWorkflow(final JiraWorkflow workflow) throws JiraException {
        if (workflow != null) {
            ValidatorDescriptor validator = workflowDescriptorsFactory.createIssueDueDateValidator();
            workflowConfigurator.addToDraft(workflow, validator, WorkflowConstants.CREATE_ACTION_NAME);

            List<String> statusesWhichBlocks = utils.getStatusesFromCategory(workflow, WorkflowConstants.COMPLETE_STATUS_CATEGORY_NAME);
            ConditionDescriptor condition = workflowDescriptorsFactory.createSubTaskBlockingCondition(statusesWhichBlocks);
            workflowConfigurator.addToDraft(workflow, condition, WorkflowConstants.DONE_STATUS_NAME);
            workflowConfigurator.addToDraft(workflow, condition, WorkflowConstants.RESOLVED_STATUS_NAME);

            workflowConfigurator.publishDraft(workflow);
        } else {
            throw new JiraException();
        }
    }
}
