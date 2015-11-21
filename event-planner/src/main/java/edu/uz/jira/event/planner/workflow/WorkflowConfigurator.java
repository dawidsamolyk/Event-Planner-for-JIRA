package edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.condition.SubTaskBlockingCondition;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.DescriptorFactory;
import com.opensymphony.workflow.loader.FunctionDescriptor;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Created by Dawid on 2015-11-18.
 */
public class WorkflowConfigurator {
    private final WorkflowTransitionService WORKFLOW_TRANSITION_SERVICE;

    public WorkflowConfigurator(@Nonnull WorkflowTransitionService workflowTransitionService) {
        this.WORKFLOW_TRANSITION_SERVICE = workflowTransitionService;
    }

    public void addSubTaskBlockingCondition(@Nonnull JiraWorkflow workflow, @Nonnull List<String> statusesWhichBlocks, @Nonnull String... transitionsNames) {
        ConditionDescriptor conditionDescriptor = createSubTaskBlockingConditionDescriptor(statusesWhichBlocks);

        for (String eachTransitionName : transitionsNames) {
            WORKFLOW_TRANSITION_SERVICE.addConditionToWorkflow(eachTransitionName, conditionDescriptor, workflow);
        }
    }

    public void addUpdateDueDatePostFunction(@Nonnull JiraWorkflow workflow, @Nonnull String... transitionsNames) {
        FunctionDescriptor postFunctionDescriptor = createUpdateDueDatePostFunctionDescriptor();

        for (String eachTransitionName : transitionsNames) {
            WORKFLOW_TRANSITION_SERVICE.addPostFunctionToWorkflow(eachTransitionName, postFunctionDescriptor, workflow);
        }
    }

    public static FunctionDescriptor createUpdateDueDatePostFunctionDescriptor() {
        FunctionDescriptor postFunctionDescriptor = DescriptorFactory.getFactory().createFunctionDescriptor();
        postFunctionDescriptor.setType("class");

        final Map functionArguments = postFunctionDescriptor.getArgs();
        functionArguments.put("class.name", UpdateDueDatePostFunction.class.getName());

        return postFunctionDescriptor;
    }

    public static ConditionDescriptor createSubTaskBlockingConditionDescriptor(List<String> statusesToBlock) {
        ConditionDescriptor conditionDescriptor = DescriptorFactory.getFactory().createConditionDescriptor();
        conditionDescriptor.setType("class");

        final Map functionArguments = conditionDescriptor.getArgs();
        functionArguments.put("class.name", SubTaskBlockingCondition.class.getName());

        StringBuilder statusesToBlockAsText = new StringBuilder();
        for (String eachStatus : statusesToBlock) {
            statusesToBlockAsText.append(eachStatus).append(",");
        }
        int lastCharIndex = statusesToBlockAsText.length() - 1;
        if (lastCharIndex > 0) {
            statusesToBlockAsText.deleteCharAt(lastCharIndex);
        }

        functionArguments.put("statuses", statusesToBlockAsText);

        return conditionDescriptor;
    }
}
