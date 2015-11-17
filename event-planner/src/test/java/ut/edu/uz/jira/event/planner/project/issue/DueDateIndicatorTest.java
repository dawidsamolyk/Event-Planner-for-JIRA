package ut.edu.uz.jira.event.planner.project.issue;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import edu.uz.jira.event.planner.project.issue.DueDateIndicator;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * Created by Dawid on 2015-11-17.
 */
public class DueDateIndicatorTest {
    @Test
    public void daysAwayFromDueDateShouldBeCalculatedProperlyForDueDateSettedToTomorrow() {
        JiraHelper mockJiraHelper = getMockJiraHelperWithIssueWithDueDate(1);
        DueDateIndicator indicator = new DueDateIndicator();

        Map contextMap = indicator.getContextMap(null, mockJiraHelper);

        int daysAwayFromDueDate = (Integer) contextMap.get("daysAwayFromDueDate");
        assertEquals(1 + 1, daysAwayFromDueDate);
    }

    @Test
    public void daysAwayFromDueDateShouldBeCalculatedProperlyForDueDateSettedToYesterday() {
        JiraHelper mockJiraHelper = getMockJiraHelperWithIssueWithDueDate(-1);
        DueDateIndicator indicator = new DueDateIndicator();

        Map contextMap = indicator.getContextMap(null, mockJiraHelper);

        int daysAwayFromDueDate = (Integer) contextMap.get("daysAwayFromDueDate");
        assertEquals(-1 + 1, daysAwayFromDueDate);
    }

    private JiraHelper getMockJiraHelperWithIssueWithDueDate(int numberOfDaysFromToday) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, numberOfDaysFromToday);
        Date tomorrow = calendar.getTime();
        Timestamp issueDueDateIsTommorow = new Timestamp(tomorrow.getTime());

        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getDueDate()).thenReturn(issueDueDateIsTommorow);

        Map<String, Object> mockContextParams = new HashMap<String, Object>();
        mockContextParams.put("issue", mockIssue);
        JiraHelper mockJiraHelper = mock(JiraHelper.class);
        Mockito.when(mockJiraHelper.getContextParams()).thenReturn(mockContextParams);
        return mockJiraHelper;
    }
}