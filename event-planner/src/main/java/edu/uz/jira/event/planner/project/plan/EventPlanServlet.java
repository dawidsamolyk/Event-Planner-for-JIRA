package edu.uz.jira.event.planner.project.plan;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.templaterenderer.TemplateRenderer;
import edu.uz.jira.event.planner.project.plan.model.Domain;
import edu.uz.jira.event.planner.project.plan.model.Plan;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventPlanServlet extends HttpServlet {
    private final EventPlanService eventPlanService;
    private final TemplateRenderer templateRenderer;
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;

    public EventPlanServlet(@Nonnull final EventPlanService eventPlanService,
                            @Nonnull final TemplateRenderer templateRenderer,
                            @Nonnull final UserManager userManager,
                            @Nonnull final LoginUriProvider loginUriProvider) {
        this.eventPlanService = eventPlanService;
        this.templateRenderer = templateRenderer;
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
    }

    /**
     * @return Event Organization Domain names (eg. Development).
     */
    private List<Domain> getDomains() {
        return eventPlanService.get(Domain.class);
    }

    /**
     * @return Event Organization Plans.
     */
    private List<Plan> getAllEventOrganziationPlans() {
        return eventPlanService.get(Plan.class);
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        UserProfile user = userManager.getRemoteUser(request);
        if (user == null || !userManager.isSystemAdmin(user.getUserKey())) {
            redirectToLogin(request, response);
            return;
        }

        eventPlanService.clearDatabase();

        Map<String, Object> context = new HashMap<String, Object>();
        context.put("DOMAINS", getDomains());
        context.put("PLANS", getAllEventOrganziationPlans());
        context.put("SERVICE", eventPlanService);

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