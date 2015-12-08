package ut.edu.uz.jira.event.planner.workflow;

import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import edu.uz.jira.event.planner.workflow.WorkflowDescriptorsFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WorkflowDescriptorsFactoryTest {

    @Test
    public void subTaskBlockingConditionDescriptorShouldContainsStatusesSeparatedByComma() {
        WorkflowDescriptorsFactory fixture = new WorkflowDescriptorsFactory();
        String expectedResult = "1203,90,123";
        List<String> statusesToBlock = new ArrayList<String>(3);
        statusesToBlock.add("1203");
        statusesToBlock.add("90");
        statusesToBlock.add("123");

        ConditionDescriptor result = fixture.createSubTaskBlockingConditionDescriptor(statusesToBlock);

        assertEquals(expectedResult, result.getArgs().get("statuses"));
    }

    @Test
    public void subTaskBlockingConditionDescriptorShouldBeCreatedEventWhenInputIsEmpty() {
        WorkflowDescriptorsFactory fixture = new WorkflowDescriptorsFactory();
        String expectedResult = "";
        List<String> statusesToBlock = new ArrayList<String>();

        ConditionDescriptor result = fixture.createSubTaskBlockingConditionDescriptor(statusesToBlock);

        assertEquals(expectedResult, result.getArgs().get("statuses"));
    }

    @Test
    public void subTaskBlockingConditionDescriptorShouldBeCreatedEventWhenInputIsNull() {
        WorkflowDescriptorsFactory fixture = new WorkflowDescriptorsFactory();
        String expectedResult = "";

        ConditionDescriptor result = fixture.createSubTaskBlockingConditionDescriptor(null);

        assertEquals(expectedResult, result.getArgs().get("statuses"));
    }

    @Test
    public void subTaskBlockingConditionShouldNotBeNull() {
        WorkflowDescriptorsFactory fixture = new WorkflowDescriptorsFactory();
        ConditionDescriptor result = fixture.createSubTaskBlockingConditionDescriptor(null);

        assertNotNull(result);
    }

    @Test
    public void updateDueDatePostFunctionShouldNotBeNull() {
        WorkflowDescriptorsFactory fixture = new WorkflowDescriptorsFactory();
        FunctionDescriptor result = fixture.createUpdateDueDatePostFunctionDescriptor();

        assertNotNull(result);
    }

    @Test
    public void issueDueDateValidatorShouldNotBeNull() {
        WorkflowDescriptorsFactory fixture = new WorkflowDescriptorsFactory();
        ValidatorDescriptor result = fixture.createIssueDueDateValidatorDescriptor();

        assertNotNull(result);
    }
}
