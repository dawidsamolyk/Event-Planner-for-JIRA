package edu.uz.jira.event.planner.project;

import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.blueprint.api.*;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.workflow.JiraWorkflow;
import edu.uz.jira.event.planner.exceptions.NullArgumentException;
import edu.uz.jira.event.planner.project.issue.IssueFieldsConfigurator;
import edu.uz.jira.event.planner.workflow.WorkflowConfigurator;

import javax.annotation.Nonnull;
import java.util.List;

public class EventOrganizationProjectHook implements AddProjectHook {
    private static final String EVENT_ORGANIZATION_WORKFLOW_KEY = "EVENT-ORGANIZATION-WORKFLOW";
    private static final String REDIRECT_URL = "/secure/EventOrganizationPlanConfiguration.jspa?project-key=";
    private static final IssueFieldsConfigurator ISSUE_FIELDS_CONFIGURATOR = new IssueFieldsConfigurator();
    private final WorkflowConfigurator WORKFLOW_CONFIGURATOR;

    public EventOrganizationProjectHook(@Nonnull final WorkflowTransitionService workflowTransitionService) throws NullArgumentException {
        WORKFLOW_CONFIGURATOR = new WorkflowConfigurator(workflowTransitionService);
    }

    @Override
    public ValidateResponse validate(@Nonnull final ValidateData validateData) {
        validateData.projectKey();

        return ValidateResponse.create();
    }

    @Override
    public ConfigureResponse configure(@Nonnull final ConfigureData configureData) {
        Project project = configureData.project();
        ISSUE_FIELDS_CONFIGURATOR.addFieldConfigurationScheme(project);

        JiraWorkflow workflow = configureData.createdWorkflows().get(EVENT_ORGANIZATION_WORKFLOW_KEY);
        configureWorkflow(workflow);

        return ConfigureResponse.create().setRedirect(REDIRECT_URL + project.getKey());
    }

    private void configureWorkflow(@Nonnull final JiraWorkflow workflow) {
        if (workflow != null) {
            WORKFLOW_CONFIGURATOR.addUpdateDueDatePostFunctionToTransitions(workflow, "Deadline exceeded");

            List<String> statusesWhichBlocks = WORKFLOW_CONFIGURATOR.getStatusesFromCategory(workflow, "Complete");
            WORKFLOW_CONFIGURATOR.addSubTaskBlockingCondition(workflow, statusesWhichBlocks, "Done");
        }
    }
}
