package edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.database.ActiveObjectsService;
import edu.uz.jira.event.planner.project.plan.rest.EventRestConfiguration;
import edu.uz.jira.event.planner.project.plan.rest.RestManagerHelper;
import net.java.ao.Entity;
import net.java.ao.Query;
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
    protected final ActiveObjectsService activeObjectsService;
    protected final Class entityType;
    private final TransactionTemplate transactionTemplate;
    private final EventRestConfiguration emptyConfiguration;
    private final UserManager userManager;
    private final RestManagerHelper helper;

    /**
     * Constructor.
     *
     * @param userManager          Injected {@code UserManager} implementation.
     * @param transactionTemplate  Injected {@code TransactionTemplate} implementation.
     * @param activeObjectsService Event Organization Plan Service which manages Active Objects (Plans, Domains, Tasks etc.).
     * @param entityType           Type of Entities which this RestManages can handle.
     * @param emptyConfiguration   Empty configuration of handled Entities.
     */
    public RestManager(@Nonnull final UserManager userManager,
                       @Nonnull final TransactionTemplate transactionTemplate,
                       @Nonnull final ActiveObjectsService activeObjectsService,
                       @Nonnull final Class entityType,
                       @Nonnull final EventRestConfiguration emptyConfiguration) {
        this.userManager = userManager;
        this.transactionTemplate = transactionTemplate;
        this.activeObjectsService = activeObjectsService;
        this.entityType = entityType;
        this.emptyConfiguration = emptyConfiguration;
        helper = new RestManagerHelper(userManager);
    }

    /**
     * Handles PUT request.
     *
     * @param resource Resource with data to post.
     * @param request  Http Servlet request.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     */
    public Response post(final EventRestConfiguration resource, @Context final HttpServletRequest request) {
        if (!helper.isAdminUser(userManager.getRemoteUser(request))) {
            return helper.buildStatus(Response.Status.UNAUTHORIZED);
        }
        if (resource == null) {
            return helper.buildStatus(Response.Status.NO_CONTENT);
        }
        if (!resource.isFullfilled()) {
            return helper.buildStatus(Response.Status.NOT_ACCEPTABLE);
        }
        return doPutTransaction(resource);
    }

    /**
     * Handles POST request.
     *
     * @param id      Id of Entity to get.
     * @param request Http Servlet request.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     */
    public Response post(final String id, @Context final HttpServletRequest request) {
        if (!helper.isAdminUser(userManager.getRemoteUser(request))) {
            return helper.buildStatus(Response.Status.UNAUTHORIZED);
        }
        if (id == null || id.isEmpty()) {
            return helper.buildStatus(Response.Status.NOT_ACCEPTABLE);
        }
        return Response.ok(transactionTemplate.execute(new TransactionCallback<EventRestConfiguration[]>() {
            public EventRestConfiguration[] doInTransaction() {
                return getEntities(entityType, Query.select().where("ID = ?", id));
            }
        })).build();
    }

    /**
     * Handles GET request.
     *
     * @param request Http Servlet request.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     */
    public Response get(@Context final HttpServletRequest request) {
        if (!helper.isAdminUser(userManager.getRemoteUser(request))) {
            return helper.buildStatus(Response.Status.UNAUTHORIZED);
        }
        return Response.ok(transactionTemplate.execute(new TransactionCallback<EventRestConfiguration[]>() {
            public EventRestConfiguration[] doInTransaction() {
                return getEntities(entityType, Query.select());
            }
        })).build();
    }

    public Response delete(@Nonnull final Class<? extends RawEntity> type, @Nonnull final String id, @Nonnull final HttpServletRequest request) {
        if (!helper.isAdminUser(userManager.getRemoteUser(request))) {
            return helper.buildStatus(Response.Status.UNAUTHORIZED);
        }
        if (!activeObjectsService.delete(type, id)) {
            return helper.buildStatus(Response.Status.NOT_FOUND);
        }
        return helper.buildStatus(Response.Status.OK);
    }

    private Response doPutTransaction(@Nonnull final EventRestConfiguration resource) {
        return transactionTemplate.execute(new TransactionCallback<Response>() {
            public Response doInTransaction() {
                if (!resource.getWrappedType().equals(entityType)) {
                    return helper.buildStatus(Response.Status.NOT_ACCEPTABLE);
                }
                Entity result = activeObjectsService.addFrom(resource);
                return checkArgumentAndResponse(result);
            }
        });
    }

    private EventRestConfiguration[] getEntities(@Nonnull final Class<? extends RawEntity> entityType, @Nonnull final Query query) {
        List<? extends RawEntity> entities = activeObjectsService.get(entityType, query);
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

    private EventRestConfiguration createFrom(@Nonnull final Entity entity) {
        return emptyConfiguration.getEmptyCopy().fill(entity);
    }

    private Response checkArgumentAndResponse(final Entity entity) {
        if (entity == null) {
            return helper.buildStatus(Response.Status.NOT_ACCEPTABLE);
        }
        return helper.buildStatus(Response.Status.ACCEPTED);
    }
}
