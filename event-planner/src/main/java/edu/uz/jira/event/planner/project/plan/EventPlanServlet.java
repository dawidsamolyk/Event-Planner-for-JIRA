package edu.uz.jira.event.planner.project.plan;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import edu.uz.jira.event.planner.project.plan.model.*;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet which handles Event Plan Configuration page.
 */
public final class EventPlanServlet extends HttpServlet {
    private final ActiveObjectsService activeObjectsService;
    private final TemplateRenderer templateRenderer;
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;

    /**
     * Constructor.
     *
     * @param activeObjectsService Injected {@code ActiveObjectsService} implementation.
     * @param templateRenderer Injected {@code TemplateRenderer} implementation.
     * @param userManager      Injected {@code UserManager} implementation.
     * @param loginUriProvider Injected {@code LoginUriProvider} implementation.
     */
    public EventPlanServlet(@Nonnull final ActiveObjectsService activeObjectsService,
                            @Nonnull final TemplateRenderer templateRenderer,
                            @Nonnull final UserManager userManager,
                            @Nonnull final LoginUriProvider loginUriProvider) {
        this.activeObjectsService = activeObjectsService;
        this.templateRenderer = templateRenderer;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        UserProfile user = userManager.getRemoteUser(request);
        if (user == null || !userManager.isSystemAdmin(user.getUserKey())) {
            redirectToLogin(request, response);
            return;
        }

        Map<String, Object> context = new HashMap<String, Object>();
        context.put("DOMAINS", activeObjectsService.get(Domain.class));
        context.put("PLANS", activeObjectsService.get(Plan.class));
        context.put("COMPONENTS", activeObjectsService.get(Component.class));
        context.put("TASKS", activeObjectsService.get(Task.class));
        context.put("SUBTASKS", activeObjectsService.get(SubTask.class));

        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("/templates/admin/event-plans.vm", context, response.getWriter());
    }

    private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
    }

    private URI getUri(HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }
}