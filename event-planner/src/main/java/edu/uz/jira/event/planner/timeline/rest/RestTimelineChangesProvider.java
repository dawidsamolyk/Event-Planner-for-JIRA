package edu.uz.jira.event.planner.timeline.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
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
import java.util.*;

/**
 * REST manager which provides information about project changes for Time Line.
 */
@Path("/changes")
public class RestTimelineChangesProvider {
    private final TransactionTemplate transactionTemplate;
    private final RestManagerHelper helper;
    private final ProjectManager projectManager;
    private final IssueManager issueManager;
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
        issueManager = ComponentAccessor.getIssueManager();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(final TimeLineChangesConfiguration configuration, @Context final HttpServletRequest request) {
        if (!configuration.isFullfilled()) {
            return helper.buildStatus(Response.Status.NOT_ACCEPTABLE);
        }
        Date lastRequestTime = configuration.getLastRequestTime();
        List<String> currentTasksKeys = new ArrayList<String>(configuration.getCurrentTasksKeys());

        Project project = projectManager.getProjectByCurrentKeyIgnoreCase(configuration.getProjectKey());
        if (project == null) {
            return helper.buildStatus(Response.Status.NOT_FOUND);
        }

        Collection<Long> issuesIds;
        try {
            issuesIds = issueManager.getIssueIdsForProject(project.getId());
        } catch (GenericEntityException e) {
            return helper.buildStatus(Response.Status.NOT_FOUND);
        }

        final List<String> changedIssuesKeys = new ArrayList<String>();
        for (Issue each : issueManager.getIssueObjects(issuesIds)) {
            List<ChangeHistory> changeHistory = changeHistoryManager.getChangeHistoriesSince(each, lastRequestTime);

            if (!changeHistory.isEmpty() || each.getCreated().after(lastRequestTime)) {
                changedIssuesKeys.add(each.getKey());
            }

            currentTasksKeys.remove(each.getKey());
        }
        if(!currentTasksKeys.isEmpty()) {
            changedIssuesKeys.addAll(currentTasksKeys);
        }

        if (changedIssuesKeys.isEmpty()) {
            return helper.buildStatus(Response.Status.NO_CONTENT);
        }
        return Response.ok(new GenericEntity<List<String>>(changedIssuesKeys) {
        }).build();
    }

    public static class TimeLineChangesConfiguration {
        private String projectKey;
        private long lastRequestTime;
        private String[] currentTasksKeys;

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

        public List<String> getCurrentTasksKeys() {
            return Arrays.asList(currentTasksKeys);
        }

        public void setCurrentTasksKeys(String[] currentTasksKeys) {
            this.currentTasksKeys = currentTasksKeys;
        }
    }
}
