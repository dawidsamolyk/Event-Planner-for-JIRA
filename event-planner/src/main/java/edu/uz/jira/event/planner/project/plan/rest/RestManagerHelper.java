package edu.uz.jira.event.planner.project.plan.rest;

import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.util.ServletHelper;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;

/**
 * Helper for REST Managers.
 */
public class RestManagerHelper extends ServletHelper {

    /**
     * Constructor.
     */
    public RestManagerHelper() {
        super(null);
    }

    /**
     * Constructor.
     *
     * @param userManager Injected {@code UserManager} implementation.
     */
    public RestManagerHelper(@Nonnull final UserManager userManager) {
        super(userManager);
    }

    public Response buildStatus(@Nonnull final Response.Status status) {
        return Response.status(status).build();
    }
}
