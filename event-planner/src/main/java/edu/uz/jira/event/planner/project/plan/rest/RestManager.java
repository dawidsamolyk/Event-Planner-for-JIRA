package edu.uz.jira.event.planner.project.plan.rest;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import edu.uz.jira.event.planner.exceptions.ResourceException;
import edu.uz.jira.event.planner.project.plan.EventOrganizationPlanService;
import net.java.ao.Entity;
import net.java.ao.RawEntity;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

/**
 * Abstract REST manager which implements common functionalities.
 */
@Path("/")
public abstract class RestManager {
    protected final TransactionTemplate transactionTemplate;
    protected final EventOrganizationPlanService eventPlanService;
    private final UserManager userManager;

    public RestManager(@Nonnull final UserManager userManager,
                       @Nonnull final TransactionTemplate transactionTemplate,
                       @Nonnull final EventOrganizationPlanService eventPlanService) {
        this.userManager = userManager;
        this.transactionTemplate = transactionTemplate;
        this.eventPlanService = eventPlanService;
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final EventConfig resource, @Context final HttpServletRequest request) {
        if (!isAdminUser(userManager.getRemoteUser(request))) {
            return buildStatus(Response.Status.UNAUTHORIZED);
        }
        if (resource == null) {
            return buildStatus(Response.Status.NO_CONTENT);
        }
        if (!resource.isFullfilled()) {
            return buildStatus(Response.Status.NOT_ACCEPTABLE);
        }

        final TransactionResult result = doPutTransaction(resource);

        if (result.isValid()) {
            return buildStatus(Response.Status.OK);
        }
        return buildStatus(Response.Status.INTERNAL_SERVER_ERROR);
    }

    private Response buildStatus(@Nonnull final Response.Status status) {
        return Response.status(status).build();
    }

    private TransactionResult doPutTransaction(@Nonnull final EventConfig resource) {
        final TransactionResult result = new TransactionResult();

        transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction() {
                try {
                    doPut(resource);
                    result.setValid();
                } catch (ResourceException e) {
                    result.setError();
                }
                return null;
            }
        });

        return result;
    }

    protected abstract void doPut(@Nonnull final EventConfig resource) throws ResourceException;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context final HttpServletRequest request) {
        if (!isAdminUser(userManager.getRemoteUser(request))) {
            return buildStatus(Response.Status.UNAUTHORIZED);
        }
        return Response.ok(transactionTemplate.execute(new TransactionCallback() {
            public Object doInTransaction() {
                return doGet(request.getParameterMap());
            }
        })).build();
    }

    protected abstract EventConfig doGet(@Nonnull final Map parameterMap);

    protected EventConfig doGetById(@Nonnull final Map parameterMap, @Nonnull final Class<? extends RawEntity> entityType, @Nonnull final EventConfig emptyResult) {
        String values[] = (String[]) parameterMap.get("id");
        if (values == null || values.length < 1) {
            return emptyResult;
        }

        String id = values[0];
        if (id == null || id.isEmpty()) {
            return emptyResult;
        }
        Integer idAsNumber = Integer.parseInt(id);

        List<? extends RawEntity> entities = eventPlanService.get(entityType);

        for (RawEntity eachEntity : entities) {
            if (eachEntity instanceof Entity) {
                Entity entity = (Entity) eachEntity;
                if (idAsNumber.equals(entity.getID())) {
                    return createFrom(entity);
                }
            }

        }
        return emptyResult;
    }

    protected abstract EventConfig createFrom(Entity entity);

    private boolean isAdminUser(final UserProfile user) {
        return user != null && userManager.isSystemAdmin(user.getUserKey());
    }
}
