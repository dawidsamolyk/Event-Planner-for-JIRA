package ut.edu.uz.jira.event.planner.workflow.postfunctions;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.workflow.DefaultWorkflowSchemeManager;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.jira.workflow.WorkflowSchemeManager;
import edu.uz.jira.event.planner.workflow.postfunctions.UpdateDueDatePostFunction;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class UpdateDueDatePostFunctionTest {

    @Before
    public void setUp() {
        new MockComponentWorker()
                .addMock(ComponentAccessor.class, mock(ComponentAccessor.class))
                .addMock(IssueService.class, mock(IssueService.class))
                .addMock(WorkflowManager.class, mock(WorkflowManager.class))
                .addMock(WorkflowSchemeManager.class, mock(DefaultWorkflowSchemeManager.class))
                .init();
    }

    @Test
    public void dueDateShouldBeChangedIfItIsAfterCurrentDay() throws Exception {
        final MockIssue mockIssue = new MockIssue();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        Timestamp tomorrowTime = new Timestamp(tomorrow.getTime());
        mockIssue.setDueDate(tomorrowTime);
        UpdateDueDatePostFunction function = new UpdateDueDatePostFunction() {
            protected MutableIssue getIssue(Map transientVars) {
                return mockIssue;
            }
        };

        function.execute(new HashMap(), null, null);

        assertTrue(mockIssue.getDueDate().before(tomorrowTime));
    }

    @Test
    public void dueDateShouldNotBeChangedWhenDueDateIsBeforeCurrentDay() throws Exception {
        final MockIssue mockIssue = new MockIssue();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = calendar.getTime();
        Timestamp yesterdayTime = new Timestamp(yesterday.getTime());
        mockIssue.setDueDate(yesterdayTime);
        UpdateDueDatePostFunction function = new UpdateDueDatePostFunction() {
            protected MutableIssue getIssue(Map transientVars) {
                return mockIssue;
            }
        };

        function.execute(new HashMap(), null, null);

        assertTrue(mockIssue.getDueDate().equals(yesterdayTime));
    }
}
