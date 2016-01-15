package edu.uz.jira.event.planner.util;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * Helper for Servlets.
 */
public class ServletHelper {
    private final UserManager userManager;
    private com.atlassian.sal.api.auth.LoginUriProvider loginUriProvider;

    /**
     * Constructor.
     *
     * @param userManager Injected {@code UserManager} implementation.
     */
    protected ServletHelper(@Nonnull final UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Constructor.
     *
     * @param userManager Injected {@code UserManager} implementation.
     * @param userManager loginUriProvider {@code LoginUriProvider} implementation.
     */
    public ServletHelper(@Nonnull final UserManager userManager, @Nonnull final LoginUriProvider loginUriProvider) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
    }

    /**
     * @param user User profile.
     * @return Indicates that user is not admin.
     */
    public boolean isNotAdminUser(final UserProfile user) {
        return user == null || userManager == null || !userManager.isSystemAdmin(user.getUserKey());
    }

    /**
     * @param request HTTP request with user who invokes it.
     * @return Indicates that user who invokes input HTTP request is not admin.
     */
    public boolean hasNotAdminUser(final HttpServletRequest request) {
        if (request == null || userManager == null) {
            return true;
        }
        UserProfile user = userManager.getRemoteUser(request);
        return isNotAdminUser(user);
    }

    /**
     * @param request  HTTP request with user who invokes it.
     * @param response HTTP response.
     * @throws IOException Thrown when cannot send redirect.
     */
    public void redirectToLogin(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (loginUriProvider != null) {
            URI uri = getUri(request);
            String loginUriString = loginUriProvider.getLoginUri(uri).toASCIIString();

            response.sendRedirect(loginUriString);
        }
    }

    private URI getUri(final HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }

    /**
     * @param request HTTP request with user who invokes it.
     * @return Indicates that user who invokes input HTTP request is not JIRA user.
     */
    public boolean hasNotJiraUser(HttpServletRequest request) {
        if (userManager == null) {
            return true;
        }
        return userManager.getRemoteUser(request) == null;
    }
}
