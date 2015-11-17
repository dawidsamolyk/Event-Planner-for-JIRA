package edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.workflow.function.issue.AbstractJiraFunctionProvider;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.WorkflowException;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 * This is the post-function class that gets executed at the end of the transition.
 * Any parameters that were saved in your factory class will be available in the transientVars Map.
 */
public class UpdateDueDatePostFunction extends AbstractJiraFunctionProvider {

    public void execute(@Nonnull final Map transientVars, Map args, PropertySet ps) throws WorkflowException {
        MutableIssue issue = getIssue(transientVars);

        Timestamp currentTime = new Timestamp(new Date().getTime());

        if (currentTime.before(issue.getDueDate())) {
            issue.setDueDate(currentTime);
        }
    }
}