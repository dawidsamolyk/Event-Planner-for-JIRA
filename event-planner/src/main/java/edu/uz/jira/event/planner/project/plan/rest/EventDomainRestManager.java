package edu.uz.jira.event.planner.project.plan.rest;

import com.atlassian.activeobjects.internal.ActiveObjectsSqlException;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.exceptions.ResourceException;
import edu.uz.jira.event.planner.project.plan.EventOrganizationPlanService;
import edu.uz.jira.event.planner.project.plan.model.Domain;
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

    public EventDomainRestManager(@Nonnull final UserManager userManager,
                                  @Nonnull final TransactionTemplate transactionTemplate,
                                  @Nonnull final EventOrganizationPlanService eventPlanService) {
        super(userManager, transactionTemplate, eventPlanService);
    }

    @Override
    protected void doPut(@Nonnull final EventConfig resource) throws ResourceException {
        try {
            eventPlanService.addFrom((EventDomainConfig) resource);
        } catch (ClassCastException e) {
            throw new ResourceException(e.getMessage(), e);
        } catch (ActiveObjectsSqlException e) {
            throw new ResourceException(e.getMessage(), e);
        }
    }

    @Override
    protected EventConfig doGet(@Nonnull final Map parameterMap) {
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

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class EventDomainConfig implements EventConfig {
        @XmlElement
        private String name;
        @XmlElement
        private String description;

        public EventDomainConfig() {
            setName("");
            setDescription("");
        }

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

        @Override
        public boolean isFullfilled() {
            return getName() != null && !getName().isEmpty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EventDomainConfig that = (EventDomainConfig) o;

            if (!getName().equals(that.getName())) return false;
            return !(getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null);

        }

        @Override
        public int hashCode() {
            return getName().hashCode();
        }
    }
}
