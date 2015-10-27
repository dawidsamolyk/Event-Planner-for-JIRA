package com.atlassian.plugins.tutorial.refapp.servlet;

/**
 * Created by Dawid on 2015-10-27.
 */
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

public class EventPlannerView extends HttpServlet {
    private final UserManager userManager;
    private final LoginUriProvider loginUriProvider;
    private final TemplateRenderer templateRenderer;

    public EventPlannerView(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer templateRenderer) {
        this.userManager = userManager;
        this.loginUriProvider = loginUriProvider;
        this.templateRenderer = templateRenderer;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String username = userManager.getRemoteUsername(request);
        if (username == null || !userManager.isSystemAdmin(username)) {
            redirectToLogin(request, response);
            return;
        }

        response.setContentType("text/html;charset=utf-8");
        templateRenderer.render("week-view.vm", response.getWriter());
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