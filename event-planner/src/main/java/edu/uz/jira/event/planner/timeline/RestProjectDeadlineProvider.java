package edu.uz.jira.event.planner.timeline;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.exception.VersionNotFoundException;
import edu.uz.jira.event.planner.project.plan.rest.RestManagerHelper;
import edu.uz.jira.event.planner.util.ProjectUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;

/**
 * REST manager which provides
 */
@Path("/project/release-date")
public class RestProjectDeadlineProvider {
    public static final String PROJECT_KEY_REQUEST_PARAMETER = "project-key";
    private final TransactionTemplate transactionTemplate;
    private final RestManagerHelper helper;
    private final ProjectUtils projectUtils;

    /**
     * Constructor.
     *
     * @param transactionTemplate Injected {@code TransactionTemplate} implementation.
     * @param i18nResolver        Injected {@code I18nResolver} implementation.
     */
    public RestProjectDeadlineProvider(@Nonnull final TransactionTemplate transactionTemplate,
                                       @Nonnull final I18nResolver i18nResolver) {
        this.transactionTemplate = transactionTemplate;
        helper = new RestManagerHelper(null);
        projectUtils = new ProjectUtils(i18nResolver);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context final HttpServletRequest request) {
        final String projectKey = request.getParameter(PROJECT_KEY_REQUEST_PARAMETER);
        if (StringUtils.isBlank(projectKey)) {
            return helper.buildStatus(Response.Status.NOT_ACCEPTABLE);
        }

        final Date projectReleaseDate;
        try {
            projectReleaseDate = getProjectReleaseDate(projectKey);
        } catch (VersionNotFoundException e) {
            return helper.buildStatus(Response.Status.NOT_FOUND);
        }

        return Response.ok(transactionTemplate.execute(new TransactionCallback<Long>() {
            public Long doInTransaction() {
                return projectReleaseDate.getTime();
            }
        })).build();
    }

    public Date getProjectReleaseDate(@Nonnull final String projectKey) throws VersionNotFoundException {
        Project project = ComponentAccessor.getProjectManager().getProjectObjByKey(projectKey);

        Version dueDateVersion;
        try {
            dueDateVersion = projectUtils.getDueDateVersion(project);
        } catch (NullArgumentException e) {
            throw new VersionNotFoundException();
        }

        if (dueDateVersion == null) {
            throw new VersionNotFoundException();
        }
        return dueDateVersion.getReleaseDate();
    }
}
