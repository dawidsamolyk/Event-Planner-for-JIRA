package edu.uz.jira.event.planner.project;

import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.blueprint.api.*;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowUtil;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.DescriptorFactory;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;
import edu.uz.jira.event.planner.workflow.UpdateDueDatePostFunction;
import edu.uz.jira.event.planner.workflow.UpdateDueDatePostFunctionFactory;
import javafx.event.EventTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;

public class EventOrganizationProjectHook implements AddProjectHook {
    private final static Logger LOGGER = LoggerFactory.getLogger(EventOrganizationProjectHook.class);
    private final WorkflowTransitionService workflowTransitionService;
    private final FunctionDescriptor postFunctionDescriptor;

    public EventOrganizationProjectHook(WorkflowTransitionService workflowTransitionService) {
        this.workflowTransitionService = workflowTransitionService;
        this.postFunctionDescriptor = createUpdateDueDateDescriptor();
    }

    private FunctionDescriptor createUpdateDueDateDescriptor() {
        FunctionDescriptor postFunctionDescriptor = DescriptorFactory.getFactory().createFunctionDescriptor();
        postFunctionDescriptor.setType("class");
        final Map functionArguments = postFunctionDescriptor.getArgs();
        functionArguments.put("class.name", UpdateDueDatePostFunction.class.getName());

        UpdateDueDatePostFunctionFactory updateDueDatePostFunctionFactory = new UpdateDueDatePostFunctionFactory();
        functionArguments.putAll(updateDueDatePostFunctionFactory.getDescriptorParams((Map<String, Object>) updateDueDatePostFunctionFactory.getDescriptorParams(null)));

        return postFunctionDescriptor;
    }

    @Override
    public ValidateResponse validate(final ValidateData validateData) {
        return ValidateResponse.create();
    }

    @Override
    public ConfigureResponse configure(@Nonnull final ConfigureData configureData) {
        configureWorkflow(configureData);

        String redirect = "/browse/" + configureData.project().getKey() + "#selectedTab=com.atlassian.jira.plugin.system.project%3Asummary-panel";
        return ConfigureResponse.create().setRedirect(redirect);
    }

    private void configureWorkflow(@Nonnull ConfigureData configureData) {
        JiraWorkflow eventOrganizationWorkflow = configureData.createdWorkflows().get("EVENT-ORGANIZATION-WORKFLOW");
        if (eventOrganizationWorkflow != null) {
            workflowTransitionService.addPostFunctionToWorkflow("Deadline exceeded", postFunctionDescriptor, eventOrganizationWorkflow);
        }
    }
}
