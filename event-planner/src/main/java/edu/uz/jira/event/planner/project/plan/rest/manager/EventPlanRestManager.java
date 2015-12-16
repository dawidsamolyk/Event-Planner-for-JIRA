package edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.exception.ResourceException;
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
        super(userManager, transactionTemplate, activeObjectsService);
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
    protected Response doPut(@Nonnull final EventRestConfiguration resource) throws ResourceException {
        Plan result;
        try {
            result = activeObjectsService.addFrom((Configuration) resource);
        } catch (ClassCastException e) {
            throw new ResourceException(e.getMessage(), e);
        }
        return checkArgumentAndResponse(result);
    }

    @Override
    protected EventRestConfiguration[] doGet() {
        return doGetAll(Plan.class, Configuration.createEmpty());
    }

    @Override
    protected Configuration createFrom(@Nonnull final Entity entity) {
        if (entity instanceof Plan) {
            return new Configuration((Plan) entity);
        }
        return new Configuration();
    }

    /**
     * Event Plan Configuration in XML form.
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
         * Constructor.
         *
         * @param plan Plan database entity - source of data.
         */
        public Configuration(@Nonnull final Plan plan) {
            setName(plan.getName());
            setDescription(plan.getDescription());
            setTime(plan.getTimeToComplete());
            setDomains(ENTITY_NAME_EXTRACTOR.getNames(plan.getDomains()));
            setComponents(ENTITY_NAME_EXTRACTOR.getNames(plan.getComponents()));
        }

        /**
         * @return Event Plan Configuration with all empty fields (but not null).
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
                    && StringUtils.isNotBlank(getTime())
                    && TEXT_UTILS.isNotBlank(getDomains())
                    && TEXT_UTILS.isNotBlank(getComponents());
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

        public String[] getDomains() {
            return domains;
        }

        public void setDomains(@Nonnull String[] domains) {
            if (domains == null) {
                this.domains = new String[]{};
            } else {
                this.domains = domains;
            }
        }

        public String[] getComponents() {
            return components;
        }

        public void setComponents(String[] components) {
            if (components == null) {
                this.components = new String[]{};
            } else {
                this.components = components;
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
            if (getTime() != null ? !getTime().equals(that.getTime()) : that.getTime() != null) return false;
            if (!Arrays.equals(getDomains(), that.getDomains())) return false;
            return Arrays.equals(getComponents(), that.getComponents());
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
