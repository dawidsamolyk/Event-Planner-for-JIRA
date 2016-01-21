package edu.uz.jira.event.planner.timeline.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.project.Project;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import edu.uz.jira.event.planner.exception.IssuesNotFoundException;
import edu.uz.jira.event.planner.exception.ProjectNotFoundException;
import edu.uz.jira.event.planner.project.plan.rest.RestManagerHelper;
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
 * REST manager which provides Issues for Time Line.
 */
@Path("/issues")
public class RestIssuesProvider {
    public static final String PROJECT_KEY_REQUEST_PARAMETER = "project-key";
    private final TransactionTemplate transactionTemplate;
    private final RestManagerHelper helper;
    private final IssueManager issueManager;

    /**
     * Constructor.
     *
     * @param transactionTemplate Injected {@code TransactionTemplate} implementation.
     */
    public RestIssuesProvider(@Nonnull final TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
        issueManager = ComponentAccessor.getIssueManager();
        helper = new RestManagerHelper();
    }

    private List<IssueDecorator> getIssues(final String projectKey) throws ProjectNotFoundException, IssuesNotFoundException {
        List<IssueDecorator> result = new ArrayList<IssueDecorator>();

        Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKeyIgnoreCase(projectKey);
        if (project == null) {
            throw new ProjectNotFoundException();
        }

        Collection<Long> issueIds;
        try {
            issueIds = issueManager.getIssueIdsForProject(project.getId());
        } catch (GenericEntityException e) {
            throw new IssuesNotFoundException();
        }

        for (Issue eachIssue : issueManager.getIssueObjects(issueIds)) {
            if (!eachIssue.isSubTask()) {
                result.add(new IssueDecorator(eachIssue));
            }
        }

        if (result.isEmpty()) {
            throw new IssuesNotFoundException();
        }

        return result;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context final HttpServletRequest request) {
        final String projectKey = request.getParameter(PROJECT_KEY_REQUEST_PARAMETER);
        if (StringUtils.isBlank(projectKey)) {
            return helper.buildStatus(Response.Status.NOT_ACCEPTABLE);
        }

        final List<IssueDecorator> result;
        try {
            result = getIssues(projectKey);
        } catch (ProjectNotFoundException e) {
            return helper.buildStatus(Response.Status.NOT_FOUND);
        } catch (IssuesNotFoundException e) {
            return helper.buildStatus(Response.Status.NO_CONTENT);
        }

        return Response.ok(transactionTemplate.execute(new TransactionCallback<IssueDecorator[]>() {
            public IssueDecorator[] doInTransaction() {
                return result.toArray(new IssueDecorator[result.size()]);
            }
        })).build();
    }
}
