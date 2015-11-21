package edu.uz.jira.event.planner.project;

import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.blueprint.api.*;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.issue.status.category.StatusCategory;
import com.atlassian.jira.workflow.JiraWorkflow;
import edu.uz.jira.event.planner.project.issue.IssueFieldsConfigurator;
import edu.uz.jira.event.planner.workflow.WorkflowConfigurator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EventOrganizationProjectHook implements AddProjectHook {
    private static final String EVENT_ORGANIZATION_WORKFLOW_KEY = "EVENT-ORGANIZATION-WORKFLOW";
    private final IssueFieldsConfigurator ISSUE_FIELDS_CONFIGURATOR;
    private final WorkflowConfigurator WORKFLOW_CONFIGURATOR;

    public EventOrganizationProjectHook(WorkflowTransitionService workflowTransitionService) {
        WORKFLOW_CONFIGURATOR = new WorkflowConfigurator(workflowTransitionService);
        ISSUE_FIELDS_CONFIGURATOR = new IssueFieldsConfigurator();
    }

    @Override
    public ValidateResponse validate(final ValidateData validateData) {
        return ValidateResponse.create();
    }

    @Override
    public ConfigureResponse configure(@Nonnull final ConfigureData configureData) {
        configureWorkflow(configureData);
        ISSUE_FIELDS_CONFIGURATOR.addFieldConfigurationScheme(configureData);

        String redirect = "/browse/" + configureData.project().getKey() + "#selectedTab=com.atlassian.jira.plugin.system.project%3Asummary-panel";
        return ConfigureResponse.create().setRedirect(redirect);
    }

    private void configureWorkflow(@Nonnull ConfigureData configureData) {
        JiraWorkflow workflow = configureData.createdWorkflows().get(EVENT_ORGANIZATION_WORKFLOW_KEY);

        if (workflow != null) {
            WORKFLOW_CONFIGURATOR.addUpdateDueDatePostFunction(workflow, "Deadline exceeded");

            List<String> statusesWhichBlocks = new ArrayList<String>();
            for (Status eachStatus : workflow.getLinkedStatusObjects()) {
                StatusCategory statusCategory = eachStatus.getStatusCategory();
                if (statusCategory.getName().equals("Complete")) {
                    statusesWhichBlocks.add(eachStatus.getId());
                }
            }
            WORKFLOW_CONFIGURATOR.addSubTaskBlockingCondition(workflow, statusesWhichBlocks, "Done");
        }
    }
}
