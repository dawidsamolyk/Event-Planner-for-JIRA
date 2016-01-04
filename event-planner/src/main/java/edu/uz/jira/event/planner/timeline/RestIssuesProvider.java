package edu.uz.jira.event.planner.timeline;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.project.plan.rest.RestManagerHelper;
import edu.uz.jira.event.planner.util.IssueDecorator;
import org.apache.commons.lang.StringUtils;
import org.ofbiz.core.entity.GenericEntityException;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * REST manager which provides
 */
@Path("/issues")
public class RestIssuesProvider {
    public static final String PROJECT_KEY_REQUEST_PARAMETER = "project-key";
    private final UserManager userManager;
    private final TransactionTemplate transactionTemplate;
    private final RestManagerHelper helper;
    private final IssueManager issueManager;

    /**
     * Constructor.
     *
     * @param userManager         Injected {@code UserManager} implementation.
     * @param transactionTemplate Injected {@code TransactionTemplate} implementation.
     */
    public RestIssuesProvider(@Nonnull final UserManager userManager,
                              @Nonnull final TransactionTemplate transactionTemplate) {
        this.userManager = userManager;
        this.transactionTemplate = transactionTemplate;
        issueManager = ComponentAccessor.getIssueManager();
        helper = new RestManagerHelper(userManager);
    }

    private List<IssueDecorator> getIssues(final String projectKey) {
        List<IssueDecorator> result = new ArrayList<IssueDecorator>();

        Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKeyIgnoreCase(projectKey);

        if (project != null) {
            Collection<Long> issueIds;
            try {
                issueIds = issueManager.getIssueIdsForProject(project.getId());
            } catch (GenericEntityException e) {
                return result;
            }

            for (Issue eachIssue : issueManager.getIssueObjects(issueIds)) {
                if(!eachIssue.isSubTask()) {
                    result.add(new IssueDecorator(eachIssue));
                }
            }
        }
        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context final HttpServletRequest request) {
        if (!helper.isAdminUser(userManager.getRemoteUser(request))) {
            return helper.buildStatus(Response.Status.UNAUTHORIZED);
        }
        final String projectKey = request.getParameter(PROJECT_KEY_REQUEST_PARAMETER);
        if (StringUtils.isBlank(projectKey)) {
            return helper.buildStatus(Response.Status.NOT_ACCEPTABLE);
        }
        final List<IssueDecorator> result = getIssues(projectKey);
        if (result == null || result.isEmpty()) {
            return helper.buildStatus(Response.Status.NOT_FOUND);
        }
        return Response.ok(transactionTemplate.execute(new TransactionCallback<IssueDecorator[]>() {
            public IssueDecorator[] doInTransaction() {
                IssueDecorator[] issues = result.toArray(new IssueDecorator[result.size()]);
                return issues;
            }
        })).build();
    }
}
