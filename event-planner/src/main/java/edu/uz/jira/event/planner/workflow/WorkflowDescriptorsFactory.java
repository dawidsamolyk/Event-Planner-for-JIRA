package edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.workflow.condition.SubTaskBlockingCondition;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.DescriptorFactory;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import edu.uz.jira.event.planner.utils.TextUtils;
import edu.uz.jira.event.planner.workflow.postfunctions.UpdateDueDatePostFunction;
import edu.uz.jira.event.planner.workflow.validators.IssueDueDateValidator;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Provides descriptors for specified workflow events.
 */
public class WorkflowDescriptorsFactory {
    public static final String CLASS_NAME_KEY = "class.name";
    public static final String STATUSES_KEY = "statuses";
    public static final String TYPE_NAME = "class";
    public final TextUtils UTILS = new TextUtils();

    /**
     * @return Update Due Date Workflow Post Function.
     */
    public FunctionDescriptor createUpdateDueDatePostFunctionDescriptor() {
        FunctionDescriptor result = DescriptorFactory.getFactory().createFunctionDescriptor();
        result.setType(TYPE_NAME);

        Map functionArguments = result.getArgs();
        functionArguments.put(CLASS_NAME_KEY, UpdateDueDatePostFunction.class.getName());

        return result;
    }

    /**
     * @param statusesToBlock Workflow statuses to block condition.
     * @return Sub-Task Blocking Workflow Condition.
     */
    public ConditionDescriptor createSubTaskBlockingConditionDescriptor(@Nonnull final List<String> statusesToBlock) {
        ConditionDescriptor result = DescriptorFactory.getFactory().createConditionDescriptor();
        result.setType(TYPE_NAME);

        Map functionArguments = result.getArgs();
        functionArguments.put(CLASS_NAME_KEY, SubTaskBlockingCondition.class.getName());
        functionArguments.put(STATUSES_KEY, UTILS.getJoined(statusesToBlock, ','));

        return result;
    }

    /**
     * @return Issue Due Date Workflow Validator.
     */
    public ValidatorDescriptor createIssueDueDateValidatorDescriptor() {
        ValidatorDescriptor result = DescriptorFactory.getFactory().createValidatorDescriptor();
        result.setType(TYPE_NAME);

        Map functionArguments = result.getArgs();
        functionArguments.put(CLASS_NAME_KEY, IssueDueDateValidator.class.getName());

        return result;
    }
}
