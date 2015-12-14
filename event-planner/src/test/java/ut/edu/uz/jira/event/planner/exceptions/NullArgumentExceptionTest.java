package ut.edu.uz.jira.event.planner.exceptions;

import edu.uz.jira.event.planner.exception.NullArgumentException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NullArgumentExceptionTest {

    @Test
    public void shouldCreatesWithSingleReasonClass() {
        NullArgumentException fixture = new NullArgumentException("Test");

        assertEquals("Argument Test must not be null.", fixture.getMessage());
    }
}
