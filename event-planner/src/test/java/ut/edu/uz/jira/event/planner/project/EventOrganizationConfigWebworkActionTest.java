package ut.edu.uz.jira.event.planner.project;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.web.HttpServletVariables;
import edu.uz.jira.event.planner.project.EventOrganizationConfigWebworkAction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import webwork.action.Action;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class EventOrganizationConfigWebworkActionTest {
    private HttpServletVariables mockHttpVariables;

    @Before
    public void setup() {
        mockHttpVariables = mock(HttpServletVariables.class);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, mock(ComponentAccessor.class))
                .addMock(HttpServletVariables.class, mockHttpVariables)
                .init();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void shouldReturnInputStatusWhenAllRequiredParametersAreEmpty() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn(null);
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn(null);
        EventOrganizationConfigWebworkAction fixture = new EventOrganizationConfigWebworkAction();

        String result = fixture.execute();

        assertEquals(Action.INPUT, result);
    }

    @Test
    public void shouldReturnSuccessStatusWhenAllRequiredParametersAreNotEmpty() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("2000-01-01");
        EventOrganizationConfigWebworkAction fixture = new EventOrganizationConfigWebworkAction();

        String result = fixture.execute();

        assertEquals(Action.SUCCESS, result);
    }

    @Test
    public void shouldReturnErrorStatusWhenSomeParametersAreEmptyAndTheRestAreFilled() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn(null);
        EventOrganizationConfigWebworkAction fixture = new EventOrganizationConfigWebworkAction();

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

}
