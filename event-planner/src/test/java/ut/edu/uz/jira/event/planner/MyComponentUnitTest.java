package ut.edu.uz.jira.event.planner;

import org.junit.Test;
import edu.uz.jira.event.planner.MyPluginComponent;
import edu.uz.jira.event.planner.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}