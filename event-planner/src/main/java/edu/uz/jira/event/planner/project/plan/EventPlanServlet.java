package edu.uz.jira.event.planner.project.plan;

import com.atlassian.templaterenderer.TemplateRenderer;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class EventPlanServlet extends HttpServlet {
    private final EventOrganizationPlanService eventPlanService;
    private final TemplateRenderer templateRenderer;

    public EventPlanServlet(@Nonnull final EventOrganizationPlanService eventPlanService, @Nonnull final TemplateRenderer templateRenderer) {
        this.eventPlanService = eventPlanService;
        this.templateRenderer = templateRenderer;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        templateRenderer.render("/templates/admin/event-types.vm", response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        //final String name = req.getParameter("name");
        //eventPlanService.addPlanNamed(name);

        res.sendRedirect(req.getContextPath() + "/plugins/servlet/eventPlans/configuration");
    }


}