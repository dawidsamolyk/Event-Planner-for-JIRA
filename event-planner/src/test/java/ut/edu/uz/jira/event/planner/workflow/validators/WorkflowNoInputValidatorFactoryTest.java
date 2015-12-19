package ut.edu.uz.jira.event.planner.workflow.validators;

import edu.uz.jira.event.planner.workflow.validators.WorkflowNoInputValidatorFactory;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class WorkflowNoInputValidatorFactoryTest {
    @Test
    public void parameters_List_Should_Be_Empty() {
        WorkflowNoInputValidatorFactory fixture = new WorkflowNoInputValidatorFactory();

        assertTrue(fixture.getDescriptorParams(null).isEmpty());
    }
}
