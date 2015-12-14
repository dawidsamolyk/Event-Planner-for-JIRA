package edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.activeobjects.internal.ActiveObjectsSqlException;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.exception.ResourceException;
import edu.uz.jira.event.planner.project.plan.EventPlanService;
import edu.uz.jira.event.planner.project.plan.model.Domain;
import edu.uz.jira.event.planner.project.plan.rest.EventRestConfiguration;
import net.java.ao.Entity;

import javax.annotation.Nonnull;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

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

    @Override
    protected void doPut(@Nonnull final EventRestConfiguration resource) throws ResourceException {
        try {
            eventPlanService.addFrom((EventDomainConfig) resource);
        } catch (ClassCastException e) {
            throw new ResourceException(e.getMessage(), e);
        } catch (ActiveObjectsSqlException e) {
            throw new ResourceException(e.getMessage(), e);
        }
    }

    @Override
    protected EventRestConfiguration doGet(@Nonnull final Map parameterMap) {
        return doGetById(parameterMap, Domain.class, new EventDomainConfig());
    }

    @Override
    protected EventDomainConfig createFrom(@Nonnull final Entity entity) {
        EventDomainConfig result = new EventDomainConfig();

        if (entity instanceof Domain) {
            Domain domain = (Domain) entity;

            result.setName(domain.getName());
            result.setDescription(domain.getDescription());
        }
        return result;
    }

    /**
     * Event Domain Configuration in XML form.
     */
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class EventDomainConfig implements EventRestConfiguration {
        @XmlElement
        private String name;
        @XmlElement
        private String description;

        /**
         * Constructor.
         * Fills all fields with an empty String.
         */
        public EventDomainConfig() {
            setName("");
            setDescription("");
        }

        /**
         * @return Event Domain Configuration with all empty fields (but not null).
         */
        public static EventDomainConfig createEmpty() {
            return new EventDomainConfig();
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
            return getName() != null && !getName().isEmpty();
        }

        /**
         * @see {@link Object#equals(Object)}
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EventDomainConfig that = (EventDomainConfig) o;

            if (!getName().equals(that.getName())) return false;
            return !(getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null);

        }

        /**
         * @see {@link Object#hashCode()}
         */
        @Override
        public int hashCode() {
            return getName().hashCode();
        }
    }
}
