package edu.uz.jira.event.planner.project.issue;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.plugin.webfragment.contextproviders.AbstractJiraContextProvider;
import com.atlassian.jira.plugin.webfragment.model.JiraHelper;
import edu.uz.jira.event.planner.util.DatesDifferenceCalculator;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides back-end for due date indicator on Issue View.
 */
public class DueDateIndicator extends AbstractJiraContextProvider {

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
            return DatesDifferenceCalculator.getDaysDifference(new Date(), dueDate) + 1;
        }
        return 0;
    }
}
