package ut.edu.uz.jira.event.planner.timeline.rest;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.mock.security.MockAuthenticationContext;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.workflow.WorkflowManager;
import edu.uz.jira.event.planner.timeline.rest.RestIssuesModifier;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Ignore;
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
    private IssueService mockIssueService;
    private WorkflowManager mockWorkflowManager;

    @Before
    public void setUp() {
        mockIssueManager = mock(IssueManager.class);
        mockIssueService = mock(IssueService.class);
        mockWorkflowManager = mock(WorkflowManager.class);

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, mock(ComponentAccessor.class))
                .addMock(IssueManager.class, mockIssueManager)
                .addMock(JiraAuthenticationContext.class, new MockAuthenticationContext(new MockApplicationUser("test")))
                .addMock(IssueService.class, mockIssueService)
                .addMock(WorkflowManager.class, mockWorkflowManager)
                .init();
    }

    @Ignore
    @Test
    public void should_return_not_found_if_there_is_no_issue_with_specified_key() {
        Mockito.when(mockIssueManager.getIssueByKeyIgnoreCase(Mockito.anyString())).thenReturn(null);
        RestIssuesModifier fixture = new RestIssuesModifier();
        RestIssuesModifier.IssueData testIssueData = new RestIssuesModifier.IssueData("TEST-1", "todo", null);

        Response result = fixture.post(testIssueData, new MockHttpServletRequest());

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getStatus());
    }

    @Ignore
    @Test
    public void should_set_yesterday_as_due_date_if_task_was_set_to_late() {
        MockIssue testIssue = new MockIssue();
        Mockito.when(mockIssueManager.getIssueByKeyIgnoreCase(Mockito.anyString())).thenReturn(testIssue);
        RestIssuesModifier fixture = new RestIssuesModifier();
        RestIssuesModifier.IssueData testIssueData = new RestIssuesModifier.IssueData("TEST-1", "todo", null);

        Response result = fixture.post(testIssueData, new MockHttpServletRequest());

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertTrue(isYesterday(testIssue.getDueDate()));
    }

    private boolean isYesterday(Date date) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date());
        cal2.add(Calendar.DATE, -1);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    @Ignore
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
        assertTrue(DateUtils.isSameDay(testIssue.getDueDate(), new Date(testIssueData.getDueDateTime())));
    }

}
