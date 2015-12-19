package ut.edu.uz.jira.event.planner.project.plan.action;

import edu.uz.jira.event.planner.project.plan.action.OnlyShowInputAction;
import org.junit.Test;
import webwork.action.Action;

import static org.junit.Assert.assertEquals;

public class OnlyShowInputActionTest {

    @Test
    public void always_should_return_input() throws Exception {
        OnlyShowInputAction fixture = new OnlyShowInputAction();

        assertEquals(Action.INPUT, fixture.execute());
    }
}
