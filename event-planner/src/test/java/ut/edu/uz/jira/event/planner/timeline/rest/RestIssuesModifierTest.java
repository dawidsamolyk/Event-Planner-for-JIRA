package ut.edu.uz.jira.event.planner.timeline.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.mock.security.MockAuthenticationContext;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.MockApplicationUser;
import edu.uz.jira.event.planner.timeline.rest.RestIssuesModifier;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class RestIssuesModifierTest {
    private IssueManager mockIssueManager;

    @Before
    public void setUp() {
        mockIssueManager = mock(IssueManager.class);
        JiraAuthenticationContext mockAuthCtx = new MockAuthenticationContext(new MockApplicationUser("test"));

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, mock(ComponentAccessor.class))
                .addMock(IssueManager.class, mockIssueManager)
                .addMock(JiraAuthenticationContext.class, mockAuthCtx)
                .init();
    }

    @Test
    public void should_return_not_found_if_there_is_no_issue_with_specified_key() {
        Mockito.when(mockIssueManager.getIssueByKeyIgnoreCase(Mockito.anyString())).thenReturn(null);
        RestIssuesModifier fixture = new RestIssuesModifier();
        RestIssuesModifier.IssueData testIssueData = new RestIssuesModifier.IssueData("TEST-1", "todo", null);

        Response result = fixture.post(testIssueData, new MockHttpServletRequest());

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getStatus());
    }

    @Test
    public void should_set_yesterday_as_due_date_if_task_was_set_to_late() {
        MockIssue testIssue = new MockIssue();
        Mockito.when(mockIssueManager.getIssueByKeyIgnoreCase(Mockito.anyString())).thenReturn(testIssue);
        RestIssuesModifier fixture = new RestIssuesModifier();
        RestIssuesModifier.IssueData testIssueData = new RestIssuesModifier.IssueData("TEST-1", "todo", null);

        Response result = fixture.post(testIssueData, new MockHttpServletRequest());

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(testIssue.getDueDate());

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date());
        cal2.add(Calendar.DATE, -1);

        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        assertTrue(sameDay);
    }

    @Test
    public void should_set_due_date_for_task() {
        MockIssue testIssue = new MockIssue();
        Mockito.when(mockIssueManager.getIssueByKeyIgnoreCase(Mockito.anyString())).thenReturn(testIssue);
        RestIssuesModifier fixture = new RestIssuesModifier();
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 5);
        RestIssuesModifier.IssueData testIssueData = new RestIssuesModifier.IssueData("TEST-1", "todo", cal.getTimeInMillis());

        Response result = fixture.post(testIssueData, new MockHttpServletRequest());

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(testIssue.getDueDate());

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date(testIssueData.getDueDateTime()));

        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        assertTrue(sameDay);
    }

}
