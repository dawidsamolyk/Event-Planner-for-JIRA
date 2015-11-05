package ut.edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.mock.issue.MockIssue;
import com.opensymphony.module.propertyset.PropertySet;
import edu.uz.jira.event.planner.workflow.UpdateDueDatePostFunction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UpdateDueDatePostFunctionTest {

    @Test
    public void dueDateShouldBeChangedAfterUpdatePostFunction() throws Exception {
        final MockIssue mockIssue = new MockIssue();
        Timestamp testTime = new Timestamp(new Date().getTime());
        mockIssue.setDueDate(testTime);
        ;
        UpdateDueDatePostFunction function = new UpdateDueDatePostFunction() {
            protected MutableIssue getIssue(Map transientVars) {
                return mockIssue;
            }
        };

        function.execute(Mockito.anyMap(), Mockito.anyMap(), null);

        assertFalse(mockIssue.getDueDate().equals(testTime));
    }

}
