package edu.uz.jira.event.planner.timeline.rest;

import edu.uz.jira.event.planner.project.plan.rest.RestManagerHelper;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST manager which modifies Issues.
 */
@Path("/issue/modify")
public class RestIssuesModifier {
    private final RestManagerHelper helper;

    /**
     * Constructor.
     */
    public RestIssuesModifier() {
        helper = new RestManagerHelper();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(@Context final HttpServletRequest request) {
        return helper.buildStatus(Response.Status.BAD_REQUEST);
    }
}
