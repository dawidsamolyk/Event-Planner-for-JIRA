package ut.edu.uz.jira.event.planner.util;

import edu.uz.jira.event.planner.exceptions.NullArgumentException;
import edu.uz.jira.event.planner.util.Validator;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.mock;

public class ValidatorTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void shouldThrowExceptionWhenHttpServletRequestIsNull() throws NullArgumentException {
        Validator fixture = new Validator();

        exception.expect(NullArgumentException.class);
        fixture.check(null);
    }

    @Test
    public void shouldNotThrowExceptionWhenHttpServletRequestInstatiated() throws NullArgumentException {
        Validator fixture = new Validator();

        fixture.check(mock(HttpServletRequest.class));
    }
}
