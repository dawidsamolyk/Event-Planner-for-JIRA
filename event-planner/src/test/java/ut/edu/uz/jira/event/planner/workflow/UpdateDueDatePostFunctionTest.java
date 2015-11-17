package ut.edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.mock.issue.MockIssue;
import com.opensymphony.module.propertyset.PropertySet;
import edu.uz.jira.event.planner.workflow.UpdateDueDatePostFunction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpdateDueDatePostFunctionTest {

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
