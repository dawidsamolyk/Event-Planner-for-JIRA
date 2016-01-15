package edu.uz.jira.event.planner.project.plan.servlet;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.importer.xml.EventPlansImportExecutor;
import edu.uz.jira.event.planner.util.ServletHelper;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet which handles Event Plan Configuration page.
 */
public class EventPlanServlet extends HttpServlet {
    private static final String VIEW_TEMPLATE_PATH = "/templates/admin/event-plans.vm";
    private final TemplateRenderer templateRenderer;
    private final I18nResolver i18nResolver;
    private final ActiveObjectsService activeObjectsService;
    private final ServletHelper servletHelper;

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
        this.i18nResolver = i18nResolver;
        this.activeObjectsService = activeObjectsService;
        this.servletHelper = new ServletHelper(userManager, loginUriProvider);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (servletHelper.hasNotAdminUser(request)) {
            servletHelper.redirectToLogin(request, response);
            return;
        }
        importPredefinedEventPlansIfRequired();

        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render(VIEW_TEMPLATE_PATH, response.getWriter());
    }

    private void importPredefinedEventPlansIfRequired() {
        EventPlansImportExecutor importExecutor = new EventPlansImportExecutor(i18nResolver, activeObjectsService);
        importExecutor.startImport();
    }
}