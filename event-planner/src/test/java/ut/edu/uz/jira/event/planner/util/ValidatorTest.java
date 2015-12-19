package ut.edu.uz.jira.event.planner.util;

import edu.uz.jira.event.planner.exception.NullArgumentException;
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
    public void should_Throw_Exception_When_Http_Servlet_Request_Is_Null() throws NullArgumentException {
        Validator fixture = new Validator();

        exception.expect(NullArgumentException.class);
        fixture.check(null);
    }

    @Test
    public void should_Not_Throw_Exception_When_Http_Servlet_Request_Instatiated() throws NullArgumentException {
        Validator fixture = new Validator();

        fixture.check(mock(HttpServletRequest.class));
    }
}
