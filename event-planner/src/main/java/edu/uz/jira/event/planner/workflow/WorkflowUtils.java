package edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.issue.status.category.StatusCategory;
import com.atlassian.jira.workflow.JiraWorkflow;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Helpers for working with JIRA Workflow.
 */
public class WorkflowUtils {

    /**
     * @param workflow           Source Worflow.
     * @param statusCategoryName Category names of the Workflow Statuses to return.
     * @return Workflow statuses with specified category name.
     */
    public static List<String> getStatusesFromCategory(@Nonnull final JiraWorkflow workflow, @Nonnull final String statusCategoryName) {
        List<String> result = new ArrayList<String>();

        if (workflow == null || statusCategoryName == null) {
            return result;
        }

        for (Status eachStatus : workflow.getLinkedStatusObjects()) {
            StatusCategory statusCategory = eachStatus.getStatusCategory();
            if (statusCategory.getName().equals(statusCategoryName)) {
                result.add(eachStatus.getId());
            }
        }

        return result;
    }
}
