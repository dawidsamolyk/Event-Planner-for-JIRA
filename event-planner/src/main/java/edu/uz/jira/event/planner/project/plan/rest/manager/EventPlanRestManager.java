package edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.project.plan.ActiveObjectsService;
import edu.uz.jira.event.planner.project.plan.model.Plan;
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
 * REST manager for Event Organization Plans.
 */
@Path("/plan")
public class EventPlanRestManager extends RestManager {
    private static final EntityNameExtractor ENTITY_NAME_EXTRACTOR = new EntityNameExtractor();
    private static final TextUtils TEXT_UTILS = new TextUtils();

    /**
     * Constructor.
     *
     * @param userManager          Injected {@code UserManager} implementation.
     * @param transactionTemplate  Injected {@code TransactionTemplate} implementation.
     * @param activeObjectsService Event Organization Plan Service which manages Active Objects (Plans, Domains, Tasks etc.).
     */
    public EventPlanRestManager(@Nonnull final UserManager userManager,
                                @Nonnull final TransactionTemplate transactionTemplate,
                                @Nonnull final ActiveObjectsService activeObjectsService) {
        super(userManager, transactionTemplate, activeObjectsService, Plan.class, Configuration.createEmpty());
    }

    /**
     * @param id      Id of Plan to post. If not specified, all Plans will be returned.
     * @param request Http Servlet request.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     * @see {@link RestManager#post(String, HttpServletRequest)}
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(String id, @Context final HttpServletRequest request) {
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
     * @param id Id of Plan to delete. If not specified nothing should be deleted.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     * @see {@link RestManager#delete(Class, String)}
     */
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    public Response delete(String id) {
        return super.delete(entityType, id);
    }

    /**
     * Event Plan Configuration in XML form.
     */
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Configuration implements EventRestConfiguration {
        @XmlElement
        private int id;
        @XmlElement
        private String name;
        @XmlElement
        private String description;
        @XmlElement
        private String time;
        @XmlElement
        private String[] domains;
        @XmlElement
        private String[] components;

        /**
         * Constructor.
         * Fills all fields with an empty String.
         */
        public Configuration() {
            setName("");
            setDescription("");
            setTime("");
            setDomains(new String[]{});
            setComponents(new String[]{});
        }

        /**
         * @return Event Plan Configuration with all empty fields (but not null).
         */
        public static Configuration createEmpty() {
            return new Configuration();
        }

        /**
         * @see {@link EventRestConfiguration#fill(Entity)}
         */
        @Override
        public EventRestConfiguration fill(@Nonnull final Entity entity) {
            if (entity instanceof Plan) {
                Plan plan = (Plan) entity;
                setId(plan.getID());
                setName(plan.getName());
                setDescription(plan.getDescription());
                setTime(plan.getTimeToComplete());
                setDomains(ENTITY_NAME_EXTRACTOR.getNames(plan.getDomains()));
                setComponents(ENTITY_NAME_EXTRACTOR.getNames(plan.getComponents()));
            }
            return this;
        }

        /**
         * @see {@link EventRestConfiguration#getWrappedType()}
         */
        @Override
        public Class getWrappedType() {
            return Plan.class;
        }

        /**
         * @see {@link EventRestConfiguration#isFullfilled()}
         */
        @Override
        public boolean isFullfilled() {
            return StringUtils.isNotBlank(getName())
                    && getDescription() != null
                    && StringUtils.isNotBlank(getTime())
                    && TEXT_UTILS.isNotBlank(getDomains())
                    && TEXT_UTILS.isNotBlank(getComponents());
        }

        public String getName() {
            return name;
        }

        public void setName(@Nonnull final String name) {
            if (name == null) {
                this.name = "";
            } else {
                this.name = name;
            }
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(@Nonnull final String description) {
            if (description == null) {
                this.description = "";
            } else {
                this.description = description;
            }
        }

        public String getTime() {
            return time;
        }

        public void setTime(@Nonnull final String time) {
            if (time == null) {
                this.time = "";
            } else {
                this.time = time;
            }
        }

        public String[] getDomains() {
            return domains;
        }

        public void setDomains(@Nonnull final String[] domains) {
            if (domains == null) {
                this.domains = new String[]{};
            } else {
                this.domains = domains;
            }
        }

        public String[] getComponents() {
            return components;
        }

        public void setComponents(@Nonnull final String[] components) {
            if (components == null) {
                this.components = new String[]{};
            } else {
                this.components = components;
            }
        }

        public int getId() {
            return id;
        }

        public void setId(@Nonnull final int id) {
            this.id = id;
        }

        /**
         * @see {@link Object#equals(Object)}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Configuration that = (Configuration) o;

            if (getId() != that.getId()) return false;
            if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) return false;
            if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
                return false;
            if (getTime() != null ? !getTime().equals(that.getTime()) : that.getTime() != null) return false;
            if (!Arrays.equals(getDomains(), that.getDomains())) return false;
            return Arrays.equals(getComponents(), that.getComponents());
        }

        /**
         * @see {@link Object#hashCode()}
         */
        @Override
        public int hashCode() {
            int result = getId();
            result = 31 * result + (getName() != null ? getName().hashCode() : 0);
            return result;
        }
    }
}
