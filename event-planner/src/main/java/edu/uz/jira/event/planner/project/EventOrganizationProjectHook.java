package edu.uz.jira.event.planner.project;

import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.blueprint.api.*;
import com.atlassian.jira.issue.fields.layout.field.FieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.exceptions.NullArgumentException;
import edu.uz.jira.event.planner.project.issue.fields.IssueFieldsConfigurator;
import edu.uz.jira.event.planner.workflow.WorkflowConfigurator;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Validates and configures all created Event Organization Plan Projects.
 */
public class EventOrganizationProjectHook implements AddProjectHook {
    private static final String EVENT_ORGANIZATION_WORKFLOW_KEY = "EVENT-ORGANIZATION-WORKFLOW";
    private static final String REDIRECT_URL = "/secure/EventOrganizationPlanConfiguration.jspa?project-key=";
    private final IssueFieldsConfigurator ISSUE_FIELDS_CONFIGURATOR;
    private final WorkflowConfigurator WORKFLOW_CONFIGURATOR;

    /**
     * @param workflowTransitionService Service which manages workflows.
     * @param i18n                      Internationalization helper.
     * @throws NullArgumentException Thrown when any input argument is null.
     */
    public EventOrganizationProjectHook(@Nonnull final WorkflowTransitionService workflowTransitionService, @Nonnull final I18nResolver i18nResolver) throws NullArgumentException {
        WORKFLOW_CONFIGURATOR = new WorkflowConfigurator(workflowTransitionService);
        ISSUE_FIELDS_CONFIGURATOR = new IssueFieldsConfigurator(i18nResolver);
    }

    /**
     * @param validateData Data to validate.
     * @return Response which indicates that validation has completed successfully or not.
     */
    @Override
    public ValidateResponse validate(@Nonnull final ValidateData validateData) {
        return ValidateResponse.create();
    }

    /**
     * @param configureData Data to configure.
     * @return Response after configuring data (eg. redirect to specified page).
     */
    @Override
    public ConfigureResponse configure(@Nonnull final ConfigureData configureData) {
        Project project = configureData.project();
        configureFieldLayout(project);

        JiraWorkflow workflow = configureData.createdWorkflows().get(EVENT_ORGANIZATION_WORKFLOW_KEY);
        configureWorkflow(workflow);

        return ConfigureResponse.create().setRedirect(REDIRECT_URL + project.getKey());
    }

    /**
     * @param project Project to configure.
     */
    private void configureFieldLayout(@Nonnull final Project project) {
        FieldLayout fieldLayout = ISSUE_FIELDS_CONFIGURATOR.getEventOrganizationFieldLayout();
        ISSUE_FIELDS_CONFIGURATOR.storeEventOrganizationFieldLayout(fieldLayout);

        FieldLayoutScheme fieldConfigurationScheme = ISSUE_FIELDS_CONFIGURATOR.createFieldConfigurationScheme(project, fieldLayout);
        ISSUE_FIELDS_CONFIGURATOR.storeFieldConfigurationScheme(project, fieldConfigurationScheme);
    }

    /**
     * @param workflow Workflow to configure.
     */
    private void configureWorkflow(@Nonnull final JiraWorkflow workflow) {
        if (workflow != null) {
            WORKFLOW_CONFIGURATOR.addUpdateDueDatePostFunctionToTransitions(workflow, "Deadline exceeded");

            List<String> statusesWhichBlocks = WORKFLOW_CONFIGURATOR.getStatusesFromCategory(workflow, "Complete");
            WORKFLOW_CONFIGURATOR.addSubTaskBlockingCondition(workflow, statusesWhichBlocks, "Done");
        }
    }
}
