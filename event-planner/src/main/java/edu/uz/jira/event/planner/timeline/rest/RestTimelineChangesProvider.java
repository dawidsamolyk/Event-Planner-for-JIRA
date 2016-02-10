package edu.uz.jira.event.planner.timeline.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.changehistory.ChangeHistory;
import com.atlassian.jira.issue.changehistory.ChangeHistoryManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import edu.uz.jira.event.planner.project.plan.rest.RestManagerHelper;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.core.entity.GenericEntityException;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * REST manager which provides information about project changes for Time Line.
 */
@Path("/changes")
public class RestTimelineChangesProvider {
    private final TransactionTemplate transactionTemplate;
    private final RestManagerHelper helper;
    private final ProjectManager projectManager;
    private ChangeHistoryManager changeHistoryManager;

    /**
     * Constructor.
     *
     * @param transactionTemplate Injected {@code TransactionTemplate} implementation.
     */
    public RestTimelineChangesProvider(@Nonnull final TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
        helper = new RestManagerHelper();
        projectManager = ComponentAccessor.getProjectManager();
        changeHistoryManager = ComponentAccessor.getChangeHistoryManager();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(final TimeLineChangesConfiguration configuration, @Context final HttpServletRequest request) {
        if (!configuration.isFullfilled()) {
            return helper.buildStatus(Response.Status.NOT_ACCEPTABLE);
        }

        Project project = projectManager.getProjectByCurrentKeyIgnoreCase(configuration.getProjectKey());
        if (project == null) {
            return helper.buildStatus(Response.Status.NOT_FOUND);
        }

        Collection<Long> issuesIds;
        try {
            issuesIds = ComponentAccessor.getIssueManager().getIssueIdsForProject(project.getId());
        } catch (GenericEntityException e) {
            return helper.buildStatus(Response.Status.NOT_FOUND);
        }

        final List<String> changedIssuesKeys = new ArrayList<String>();
        for (Issue each : ComponentAccessor.getIssueManager().getIssueObjects(issuesIds)) {
            List<ChangeHistory> changeHistory = changeHistoryManager.getChangeHistoriesSince(each, configuration.getLastRequestTime());

            if (!changeHistory.isEmpty()) {
                changedIssuesKeys.add(each.getKey());
            }
        }

        if(changedIssuesKeys.isEmpty()) {
            return helper.buildStatus(Response.Status.NO_CONTENT);
        }

        return Response.ok(new GenericEntity<List<String>>(changedIssuesKeys) {
        }).build();
    }

    public static class TimeLineChangesConfiguration {
        private String projectKey;
        private long lastRequestTime;

        public String getProjectKey() {
            return projectKey;
        }

        public void setProjectKey(String projectKey) {
            this.projectKey = projectKey;
        }

        public Date getLastRequestTime() {
            return new Date(lastRequestTime);
        }

        public void setLastRequestTime(long lastRequestTime) {
            this.lastRequestTime = lastRequestTime;
        }

        public boolean isFullfilled() {
            return StringUtils.isNotBlank(projectKey);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TimeLineChangesConfiguration that = (TimeLineChangesConfiguration) o;

            if (lastRequestTime != that.lastRequestTime) return false;
            return projectKey != null ? projectKey.equals(that.projectKey) : that.projectKey == null;
        }

        @Override
        public int hashCode() {
            int result = projectKey != null ? projectKey.hashCode() : 0;
            result = 31 * result + (int) (lastRequestTime ^ (lastRequestTime >>> 32));
            return result;
        }
    }
}
