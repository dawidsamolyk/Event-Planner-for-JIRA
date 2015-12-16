package edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.exception.ResourceException;
import edu.uz.jira.event.planner.project.plan.EventPlanService;
import edu.uz.jira.event.planner.project.plan.model.Domain;
import edu.uz.jira.event.planner.project.plan.rest.EventRestConfiguration;
import net.java.ao.Entity;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * REST manager for Event Organization Domains.
 */
@Path("/domain")
public class EventDomainRestManager extends RestManager {

    /**
     * Constructor.
     *
     * @param userManager         Injected {@code UserManager} implementation.
     * @param transactionTemplate Injected {@code TransactionTemplate} implementation.
     * @param eventPlanService    Event Organization Plan Service which manages Active Objects (Plans, Domains, Tasks etc.).
     */
    public EventDomainRestManager(@Nonnull final UserManager userManager,
                                  @Nonnull final TransactionTemplate transactionTemplate,
                                  @Nonnull final EventPlanService eventPlanService) {
        super(userManager, transactionTemplate, eventPlanService);
    }

    /**
     * @param request Http Servlet request.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     * @see {@link RestManager#get(HttpServletRequest)}
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context final HttpServletRequest request) {
        return super.get(request);
    }

    /**
     * @param resource Resource with data to put.
     * @param request  Http Servlet request.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     * @see {@link RestManager#put(EventRestConfiguration, HttpServletRequest)}
     */
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(final Configuration resource, @Context final HttpServletRequest request) {
        return super.put(resource, request);
    }

    @Override
    protected void doPut(@Nonnull final EventRestConfiguration resource) throws ResourceException {
        try {
            eventPlanService.addFrom((Configuration) resource);
        } catch (ClassCastException e) {
            throw new ResourceException(e.getMessage(), e);
        }
    }

    @Override
    protected EventRestConfiguration[] doGet() {
        return doGetAll(Domain.class, Configuration.createEmpty());
    }

    @Override
    protected Configuration createFrom(@Nonnull final Entity entity) {
        if (entity instanceof Domain) {
            return new Configuration((Domain) entity);
        }
        return Configuration.createEmpty();
    }

    /**
     * Event Domain Configuration in XML form.
     */
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Configuration implements EventRestConfiguration {
        @XmlElement
        private String name;
        @XmlElement
        private String description;

        /**
         * Constructor.
         * Fills all fields with an empty String.
         */
        public Configuration() {
            setName("");
            setDescription("");
        }

        /**
         * Constructor.
         *
         * @param domain Domain database entity - source of data.
         */
        public Configuration(@Nonnull final Domain domain) {
            setName(domain.getName());
            setDescription(domain.getDescription());
        }

        /**
         * @return Event Domain Configuration with all empty fields (but not null).
         */
        public static Configuration createEmpty() {
            return new Configuration();
        }

        public String getName() {
            return name;
        }

        public void setName(@Nonnull String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(@Nonnull String description) {
            this.description = description;
        }

        /**
         * @see {@link EventRestConfiguration#isFullfilled()}
         */
        @Override
        public boolean isFullfilled() {
            return StringUtils.isNotBlank(name) && getDescription() != null;
        }

        /**
         * @see {@link Object#equals(Object)}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Configuration that = (Configuration) o;

            if (!getName().equals(that.getName())) return false;
            return !(getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null);
        }

        /**
         * @see {@link Object#hashCode()}
         */
        @Override
        public int hashCode() {
            return getName() != null ? getName().hashCode() : 0;
        }
    }
}
