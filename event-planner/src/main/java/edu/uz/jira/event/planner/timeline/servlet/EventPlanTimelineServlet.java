package edu.uz.jira.event.planner.timeline.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import edu.uz.jira.event.planner.util.ServletHelper;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet which handles Event Plan Timeline view.
 */
public class EventPlanTimelineServlet extends HttpServlet {
    private final TemplateRenderer templateRenderer;
    private final ServletHelper servletHelper;

    /**
     * Constructor.
     *
     * @param templateRenderer Injected {@code TemplateRenderer} implementation.
     * @param userManager      Injected {@code UserManager} implementation.
     * @param loginUriProvider Injected {@code LoginUriProvider} implementation.
     */
    public EventPlanTimelineServlet(@Nonnull final TemplateRenderer templateRenderer,
                                    @Nonnull final UserManager userManager,
                                    @Nonnull final LoginUriProvider loginUriProvider) {
        this.templateRenderer = templateRenderer;
        this.servletHelper = new ServletHelper(userManager, loginUriProvider);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (servletHelper.hasNotJiraUser(request)) {
            servletHelper.redirectToLogin(request, response);
            return;
        }
        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("/templates/timeline/view.vm", response.getWriter());
    }
}