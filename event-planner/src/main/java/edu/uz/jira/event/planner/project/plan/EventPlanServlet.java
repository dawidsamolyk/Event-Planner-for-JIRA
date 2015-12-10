package edu.uz.jira.event.planner.project.plan;

import edu.uz.jira.event.planner.project.plan.model.Plan;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public final class EventPlanServlet extends HttpServlet {
    private final EventOrganizationPlanService eventPlanService;

    public EventPlanServlet(@Nonnull final EventOrganizationPlanService eventPlanService) {
        this.eventPlanService = eventPlanService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        final PrintWriter w = res.getWriter();
        w.write("<h1>Event organization types</h1>");

        w.write("<form method=\"post\">");
        w.write("<input type=\"text\" name=\"name\" size=\"25\"/>");
        w.write("&nbsp;&nbsp;");
        w.write("<input type=\"submit\" name=\"submit\" value=\"Add\"/>");
        w.write("</form>");

        w.write("<ol>");

        for (Plan eachEventPlan : eventPlanService.getAllPlans()) {
            w.printf("<li>%s</li>", eachEventPlan.getName());
        }

        w.write("</ol>");
        w.write("<script language='javascript'>document.forms[0].elements[0].focus();</script>");

        w.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String name = req.getParameter("name");
        eventPlanService.addPlanNamed(name);

        res.sendRedirect(req.getContextPath() + "/plugins/servlet/eventPlans/configuration");
    }
}