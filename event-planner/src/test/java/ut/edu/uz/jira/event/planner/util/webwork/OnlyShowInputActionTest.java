package ut.edu.uz.jira.event.planner.util.webwork;

import edu.uz.jira.event.planner.util.webwork.OnlyShowInputAction;
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
