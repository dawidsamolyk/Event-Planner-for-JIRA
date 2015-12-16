package edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.exception.ResourceException;
import edu.uz.jira.event.planner.project.plan.EventPlanService;
import edu.uz.jira.event.planner.project.plan.model.Component;
import edu.uz.jira.event.planner.project.plan.rest.EventRestConfiguration;
import edu.uz.jira.event.planner.util.text.EntityNameExtractor;
import edu.uz.jira.event.planner.util.text.TextUtils;
import net.java.ao.Entity;
import org.apache.commons.lang3.StringUtils;

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
import java.util.Arrays;

/**
 * REST manager for Event Organization Components.
 */
@Path("/component")
public class EventComponentRestManager extends RestManager {
    private static final EntityNameExtractor ENTITY_NAME_EXTRACTOR = new EntityNameExtractor();
    private static final TextUtils TEXT_UTILS = new TextUtils();

    /**
     * Constructor.
     *
     * @param userManager         Injected {@code UserManager} implementation.
     * @param transactionTemplate Injected {@code TransactionTemplate} implementation.
     * @param eventPlanService    Event Organization Plan Service which manages Active Objects (Plans, Domains, Tasks etc.).
     */
    public EventComponentRestManager(@Nonnull final UserManager userManager,
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
    @Override
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
        return doGetAll(Component.class, Configuration.createEmpty());
    }

    @Override
    protected Configuration createFrom(@Nonnull final Entity entity) {
        if (entity instanceof Component) {
            return new Configuration((Component) entity);
        }
        return new Configuration();
    }

    /**
     * Event Component Configuration in XML form.
     */
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class Configuration implements EventRestConfiguration {
        @XmlElement
        private String name;
        @XmlElement
        private String description;
        @XmlElement
        private String[] tasks;

        /**
         * Constructor.
         * Fills all fields with an empty String.
         */
        public Configuration() {
            setName("");
            setDescription("");
            setTasks(new String[]{});
        }

        /**
         * Constructor.
         *
         * @param component Component database entity - source of data.
         */
        public Configuration(@Nonnull final Component component) {
            setName(component.getName());
            setDescription(component.getDescription());
            setTasks(ENTITY_NAME_EXTRACTOR.getNames(component.getTasks()));
        }

        /**
         * @return Event Component Configuration with all empty fields (but not null).
         */
        public static Configuration createEmpty() {
            return new Configuration();
        }

        /**
         * @see {@link EventRestConfiguration#isFullfilled()}
         */
        @Override
        public boolean isFullfilled() {
            return StringUtils.isNotBlank(getName())
                    && getDescription() != null
                    && TEXT_UTILS.isNotBlank(tasks);
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

        public String[] getTasks() {
            return tasks;
        }

        public void setTasks(String[] tasks) {
            this.tasks = tasks;
        }

        /**
         * @see {@link Object#equals(Object)}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Configuration that = (Configuration) o;

            if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
            if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
                return false;
            return Arrays.equals(getTasks(), that.getTasks());
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
