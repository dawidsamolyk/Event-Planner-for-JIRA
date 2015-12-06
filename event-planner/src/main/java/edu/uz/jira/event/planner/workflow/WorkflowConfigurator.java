package edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.issue.status.category.StatusCategory;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.condition.SubTaskBlockingCondition;
import com.opensymphony.workflow.loader.*;
import edu.uz.jira.event.planner.exceptions.NullArgumentException;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Configures workflow.
 */
public class WorkflowConfigurator {
    private static FunctionDescriptor updateDueDatePostFunction;
    private final WorkflowTransitionService WORKFLOW_TRANSITION_SERVICE;

    /**
     * @param workflowTransitionService Service which manages JIRA workflows.
     * @throws NullArgumentException Thrown when any of input argument is null.
     */
    public WorkflowConfigurator(@Nonnull final WorkflowTransitionService workflowTransitionService) throws NullArgumentException {
        this.checkNullArguments(workflowTransitionService);
        this.WORKFLOW_TRANSITION_SERVICE = workflowTransitionService;
    }

    /**
     * @return Update Due Date Workflow Post Function.
     */
    public static FunctionDescriptor createUpdateDueDatePostFunctionDescriptor() {
        if (updateDueDatePostFunction == null) {
            updateDueDatePostFunction = DescriptorFactory.getFactory().createFunctionDescriptor();
            updateDueDatePostFunction.setType("class");

            Map functionArguments = updateDueDatePostFunction.getArgs();
            functionArguments.put("class.name", UpdateDueDatePostFunction.class.getName());
        }
        return updateDueDatePostFunction;
    }

    /**
     * @param statusesToBlock Workflow statuses to block condition.
     * @return Sub-Task Blocking Workflow Condition.
     */
    public static ConditionDescriptor createSubTaskBlockingConditionDescriptor(@Nonnull final List<String> statusesToBlock) {
        ConditionDescriptor result = DescriptorFactory.getFactory().createConditionDescriptor();
        result.setType("class");

        Map functionArguments = result.getArgs();
        functionArguments.put("class.name", SubTaskBlockingCondition.class.getName());
        functionArguments.put("statuses", StringUtils.join(statusesToBlock, ','));

        return result;
    }

    /**
     * @return Issue Due Date Workflow Validator.
     */
    public static ValidatorDescriptor createIssueDueDateValidatorDescriptor() {
        ValidatorDescriptor result = DescriptorFactory.getFactory().createValidatorDescriptor();
        result.setType("class");

        Map functionArguments = result.getArgs();
        functionArguments.put("class.name", IssueDueDateValidator.class.getName());

        return result;
    }

    private void checkNullArguments(WorkflowTransitionService workflowTransitionService) throws NullArgumentException {
        if (workflowTransitionService == null) {
            throw new NullArgumentException(WorkflowTransitionService.class.getName());
        }
    }

    /**
     * @param workflow            Workflow to configure.
     * @param statusesWhichBlocks Statuses which will block Sub-Tasks.
     * @param transitionsNames    Names of the transitions to which condition should be added.
     */
    public void addSubTaskBlockingCondition(@Nonnull final JiraWorkflow workflow, @Nonnull final List<String> statusesWhichBlocks, @Nonnull final String... transitionsNames) {
        ConditionDescriptor conditionDescriptor = createSubTaskBlockingConditionDescriptor(statusesWhichBlocks);

        for (String eachTransitionName : transitionsNames) {
            WORKFLOW_TRANSITION_SERVICE.addConditionToWorkflow(eachTransitionName, conditionDescriptor, workflow);
        }
    }

    /**
     * @param workflow Workflow to configure.
     */
    public void addIssueDueDateValidator(@Nonnull final JiraWorkflow workflow) {
        ValidatorDescriptor validatorDescriptor = createIssueDueDateValidatorDescriptor();

        for(ActionDescriptor eachAction : workflow.getActionsByName("Create")) {
            eachAction.getValidators().add(validatorDescriptor);
        }
    }

    /**
     * @param workflow         Worflow to configure.
     * @param transitionsNames Names of the transitions to which condition should be added.
     */
    public void addUpdateDueDatePostFunctionToTransitions(@Nonnull final JiraWorkflow workflow, @Nonnull final String... transitionsNames) {
        FunctionDescriptor postFunctionDescriptor = createUpdateDueDatePostFunctionDescriptor();

        for (String eachTransitionName : transitionsNames) {
            WORKFLOW_TRANSITION_SERVICE.addPostFunctionToWorkflow(eachTransitionName, postFunctionDescriptor, workflow);
        }
    }

    /**
     * @param workflow           Source Worflow.
     * @param statusCategoryName Category names of the Workflow Statuses to return.
     * @return Workflow statuses with specified category name.
     */
    public List<String> getStatusesFromCategory(@Nonnull final JiraWorkflow workflow, @Nonnull final String statusCategoryName) {
        List<String> result = new ArrayList<String>();

        if (workflow == null || statusCategoryName == null) {
            return result;
        }

        for (Status eachStatus : workflow.getLinkedStatusObjects()) {
            StatusCategory statusCategory = eachStatus.getStatusCategory();
            if (statusCategory.getName().equals(statusCategoryName)) {
                result.add(eachStatus.getId());
            }
        }

        return result;
    }


}
