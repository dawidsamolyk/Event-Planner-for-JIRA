package edu.uz.jira.event.planner.workflow.postfunctions;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 * Post function executed after specified workflow transition. It updates Issue Due Date field to current day (when task was called).
 */
public class UpdateDueDatePostFunction extends AbstractJiraFunctionProvider {

    /**
     * @param transientVars Variables for restoration of the Issue structure.
     * @param args          Arguments
     * @param ps            Properties.
     * @throws WorkflowException Thrown when function cannot be applied to selected workflow.
     */
    public void execute(@Nonnull final Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        MutableIssue issue = getIssue(transientVars);

        Timestamp currentTime = new Timestamp(new Date().getTime());
        Timestamp issueDueDate = issue.getDueDate();

        if (currentTime.before(issueDueDate)) {
            issue.setDueDate(currentTime);
        }
    }
}