package ut.edu.uz.jira.event.planner.util.webwork;

import edu.uz.jira.event.planner.util.webwork.AlwaysShowInputAction;
import org.junit.Test;
import webwork.action.Action;

import static org.junit.Assert.assertEquals;

public class OnlyShowInputActionTest {

    @Test
    public void always_should_return_input() throws Exception {
        AlwaysShowInputAction fixture = new AlwaysShowInputAction();

        assertEquals(Action.INPUT, fixture.execute());
    }
}
