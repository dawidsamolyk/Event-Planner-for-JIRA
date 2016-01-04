package edu.uz.jira.event.planner.project.plan.rest;

import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;

import javax.annotation.Nonnull;
import javax.ws.rs.core.Response;

/**
 * Created by Dawid on 03.01.2016.
 */
public class RestManagerHelper {
    private final UserManager userManager;

    /**
     * Constructor.
     *
     * @param userManager Injected {@code UserManager} implementation.
     */
    public RestManagerHelper(@Nonnull final UserManager userManager) {
        this.userManager = userManager;
    }

    public boolean isAdminUser(final UserProfile user) {
        return user != null && userManager.isSystemAdmin(user.getUserKey());
    }

    public Response buildStatus(@Nonnull final Response.Status status) {
        return Response.status(status).build();
    }
}
