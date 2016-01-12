package edu.uz.jira.event.planner.project.plan.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.importer.xml.EventPlansImportExecutor;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * Servlet which handles Event Plan Configuration page.
 */
public class EventPlanServlet extends HttpServlet {
    private final TemplateRenderer templateRenderer;
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final I18nResolver i18nResolver;
    private final ActiveObjectsService activeObjectsService;

    /**
     * Constructor.
     *
     * @param templateRenderer Injected {@code TemplateRenderer} implementation.
     * @param userManager      Injected {@code UserManager} implementation.
     * @param loginUriProvider Injected {@code LoginUriProvider} implementation.
     */
    public EventPlanServlet(@Nonnull final TemplateRenderer templateRenderer,
                            @Nonnull final UserManager userManager,
                            @Nonnull final LoginUriProvider loginUriProvider,
                            @Nonnull final I18nResolver i18nResolver,
                            @Nonnull final ActiveObjectsService activeObjectsService) {
        this.templateRenderer = templateRenderer;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.i18nResolver = i18nResolver;
        this.activeObjectsService = activeObjectsService;
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        UserProfile user = userManager.getRemoteUser(request);
        if (user == null || !userManager.isSystemAdmin(user.getUserKey())) {
            redirectToLogin(request, response);
            return;
        }
        importPredefinedEventPlansIfRequired();

        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("/templates/admin/event-plans.vm", response.getWriter());
    }

    private void importPredefinedEventPlansIfRequired() {
        EventPlansImportExecutor importExecutor = new EventPlansImportExecutor(i18nResolver, activeObjectsService);
        importExecutor.startImport();
    }

    private void redirectToLogin(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
    }

    private URI getUri(final HttpServletRequest request) {
        StringBuffer builder = request.getRequestURL();
        if (request.getQueryString() != null) {
            builder.append("?");
            builder.append(request.getQueryString());
        }
        return URI.create(builder.toString());
    }
}