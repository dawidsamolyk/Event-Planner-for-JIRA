package edu.uz.jira.event.planner.project.issue;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides back-end for due date indicator on Issue View.
 */
public class DueDateIndicator extends AbstractJiraContextProvider {
    private static final int MILLIS_IN_DAY = 24 * 60 * 60 * 1000;

    /**
     * @param user       JIRA User.
     * @param jiraHelper JIRA Helper.
     * @return Map with days away from due date. If empty issue hasn't setted due date.
     */
    @Override
    public Map<String, Integer> getContextMap(final User user, final JiraHelper jiraHelper) {
        Issue currentIssue = (Issue) jiraHelper.getContextParams().get("issue");
        Timestamp dueDate = currentIssue.getDueDate();

        Map<String, Integer> contextMap = new HashMap<String, Integer>();
        contextMap.put("daysAwayFromDueDate", getDaysAwayFromDueDate(dueDate));
        return contextMap;
    }

    /**
     * @param dueDate Due Date.
     * @return Days away from Due Date. Negative if current date is
     */
    public int getDaysAwayFromDueDate(final Timestamp dueDate) {
        if (dueDate != null) {
            int currentTimeInDays = (int) (System.currentTimeMillis() / MILLIS_IN_DAY);
            int dueDateTimeInDays = (int) (dueDate.getTime() / MILLIS_IN_DAY);
            return dueDateTimeInDays - currentTimeInDays + 1;
        }
        return 0;
    }
}
