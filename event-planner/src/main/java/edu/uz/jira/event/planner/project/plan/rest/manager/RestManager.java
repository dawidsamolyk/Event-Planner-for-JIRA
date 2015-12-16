package edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import edu.uz.jira.event.planner.exception.ResourceException;
import edu.uz.jira.event.planner.project.plan.ActiveObjectsService;
import edu.uz.jira.event.planner.project.plan.rest.EventRestConfiguration;
import net.java.ao.Entity;
import net.java.ao.RawEntity;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Abstract REST manager which implements common functionalities for concrete REST managers.
 */
public abstract class RestManager {
    protected final TransactionTemplate transactionTemplate;
    protected final ActiveObjectsService activeObjectsService;
    private final UserManager userManager;

    /**
     * Constructor.
     *
     * @param userManager          Injected {@code UserManager} implementation.
     * @param transactionTemplate  Injected {@code TransactionTemplate} implementation.
     * @param activeObjectsService Event Organization Plan Service which manages Active Objects (Plans, Domains, Tasks etc.).
     */
    public RestManager(@Nonnull final UserManager userManager,
                       @Nonnull final TransactionTemplate transactionTemplate,
                       @Nonnull final ActiveObjectsService activeObjectsService) {
        this.userManager = userManager;
        this.transactionTemplate = transactionTemplate;
        this.activeObjectsService = activeObjectsService;
    }

    /**
     * Handles PUT request.
     *
     * @param resource Resource with data to put.
     * @param request  Http Servlet request.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     */
    public Response put(final EventRestConfiguration resource, @Context final HttpServletRequest request) {
        if (!isAdminUser(userManager.getRemoteUser(request))) {
            return buildStatus(Response.Status.UNAUTHORIZED);
        }
        if (resource == null) {
            return buildStatus(Response.Status.NO_CONTENT);
        }
        if (!resource.isFullfilled()) {
            return buildStatus(Response.Status.NOT_ACCEPTABLE);
        }
        return doPutTransaction(resource);
    }

    private boolean isAdminUser(final UserProfile user) {
        return user != null && userManager.isSystemAdmin(user.getUserKey());
    }

    protected Response buildStatus(@Nonnull final Response.Status status) {
        return Response.status(status).build();
    }

    private Response doPutTransaction(@Nonnull final EventRestConfiguration resource) {
        return transactionTemplate.execute(new TransactionCallback<Response>() {
            public Response doInTransaction() {
                try {
                    return doPut(resource);
                } catch (ResourceException e) {
                    return buildStatus(Response.Status.INTERNAL_SERVER_ERROR);
                }
            }
        });
    }

    protected abstract Response doPut(@Nonnull final EventRestConfiguration resource) throws ResourceException;

    /**
     * Handles GET request.
     *
     * @param request Http Servlet request.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     */
    public Response get(@Context final HttpServletRequest request) {
        if (!isAdminUser(userManager.getRemoteUser(request))) {
            return buildStatus(Response.Status.UNAUTHORIZED);
        }
        return Response.ok(transactionTemplate.execute(new TransactionCallback<EventRestConfiguration[]>() {
            public EventRestConfiguration[] doInTransaction() {
                return doGet();
            }
        })).build();
    }

    protected abstract EventRestConfiguration[] doGet();

    protected EventRestConfiguration[] doGetAll(@Nonnull final Class<? extends RawEntity> entityType, @Nonnull final EventRestConfiguration defaultResult) {
        List<? extends RawEntity> entities = activeObjectsService.get(entityType);
        int numberOfEntities = entities.size();

        EventRestConfiguration[] result = new EventRestConfiguration[numberOfEntities];

        for (int index = 0; index < numberOfEntities; index++) {
            RawEntity eachEntity = entities.get(index);

            if (eachEntity instanceof Entity) {
                result[index] = createFrom((Entity) eachEntity);
            }
        }
        return result;
    }

    protected abstract EventRestConfiguration createFrom(@Nonnull final Entity entity);

    protected Response checkArgumentAndResponse(final Entity entity) {
        if (entity == null) {
            return buildStatus(Response.Status.NOT_ACCEPTABLE);
        }
        return buildStatus(Response.Status.ACCEPTED);
    }
}
