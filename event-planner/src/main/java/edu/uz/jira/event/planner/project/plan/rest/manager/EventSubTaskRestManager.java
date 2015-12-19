package edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.project.plan.ActiveObjectsService;
import edu.uz.jira.event.planner.project.plan.model.SubTask;
import edu.uz.jira.event.planner.project.plan.rest.EventRestConfiguration;
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

/**
 * REST manager for Event Organization SubTask.
 */
@Path("/subtask")
public class EventSubTaskRestManager extends RestManager {

    /**
     * Constructor.
     *
     * @param userManager          Injected {@code UserManager} implementation.
     * @param transactionTemplate  Injected {@code TransactionTemplate} implementation.
     * @param activeObjectsService Event Organization Plan Service which manages Active Objects (Plans, Domains, Tasks etc.).
     */
    public EventSubTaskRestManager(@Nonnull final UserManager userManager,
                                   @Nonnull final TransactionTemplate transactionTemplate,
                                   @Nonnull final ActiveObjectsService activeObjectsService) {
        super(userManager, transactionTemplate, activeObjectsService, SubTask.class, Configuration.createEmpty());
    }

    /**
     * @param id      Id of SubTask to post. If not specified, all SubTasks will be returned.
     * @param request Http Servlet request.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     * @see {@link RestManager#post(String, HttpServletRequest)}
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(final String id, @Context final HttpServletRequest request) {
        return super.post(id, request);
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

    /**
     * @param id Id of SubTask to delete. If not specified nothing should be deleted.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     * @see {@link RestManager#delete(Class, String)}
     */
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    public Response delete(String id) {
        return super.delete(entityType, id);
    }

    /**
     * Event SubTask Configuration in XML form.
     */
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Configuration implements EventRestConfiguration {
        @XmlElement
        private String name;
        @XmlElement
        private String description;
        @XmlElement
        private String time;

        /**
         * Constructor.
         * Fills all fields with an empty String.
         */
        public Configuration() {
            setName("");
            setDescription("");
            setTime("");
        }

        /**
         * @return Event SubTask Configuration with all empty fields (but not null).
         */
        public static Configuration createEmpty() {
            return new Configuration();
        }

        /**
         * @see {@link EventRestConfiguration#fill(Entity)}
         */
        @Override
        public EventRestConfiguration fill(@Nonnull final Entity entity) {
            if (entity instanceof SubTask) {
                SubTask subtask = (SubTask) entity;
                setName(subtask.getName());
                setDescription(subtask.getDescription());
                setTime(subtask.getTimeToComplete());
            }
            return this;
        }

        /**
         * @see {@link EventRestConfiguration#getWrappedType()}
         */
        @Override
        public Class getWrappedType() {
            return SubTask.class;
        }

        /**
         * @see {@link EventRestConfiguration#isFullfilled()}
         */
        @Override
        public boolean isFullfilled() {
            return StringUtils.isNotBlank(getName())
                    && getDescription() != null
                    && StringUtils.isNotBlank(getTime());
        }

        public String getName() {
            return name;
        }

        public void setName(@Nonnull String name) {
            if (name == null) {
                this.name = "";
            } else {
                this.name = name;
            }
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(@Nonnull String description) {
            if (description == null) {
                this.description = "";
            } else {
                this.description = description;
            }
        }

        public String getTime() {
            return time;
        }

        public void setTime(@Nonnull String time) {
            if (time == null) {
                this.time = "";
            } else {
                this.time = time;
            }
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
            return !(getTime() != null ? !getTime().equals(that.getTime()) : that.getTime() != null);
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
