package edu.uz.jira.event.planner.project.plan.rest;

import com.atlassian.activeobjects.internal.ActiveObjectsSqlException;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.exceptions.ResourceException;
import edu.uz.jira.event.planner.project.plan.EventOrganizationPlanService;
import edu.uz.jira.event.planner.project.plan.model.Plan;
import edu.uz.jira.event.planner.util.EntityNameExtractor;
import edu.uz.jira.event.planner.util.TextUtils;
import net.java.ao.Entity;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

/**
 * REST manager for Event Organization Plans.
 */
@Path("/plan")
public class EventPlanRestManager extends RestManager {
    private static final EntityNameExtractor ENTITY_NAME_EXTRACTOR = new EntityNameExtractor();
    private static final TextUtils TEXT_UTILS = new TextUtils();

    public EventPlanRestManager(@Nonnull final UserManager userManager,
                                @Nonnull final TransactionTemplate transactionTemplate,
                                @Nonnull final EventOrganizationPlanService eventPlanService) {
        super(userManager, transactionTemplate, eventPlanService);
    }

    @Override
    protected void doPut(@Nonnull final EventConfig resource) throws ResourceException {
        try {
            eventPlanService.addFrom((EventPlanConfig) resource);
        } catch (ClassCastException e) {
            throw new ResourceException(e.getMessage(), e);
        } catch (ActiveObjectsSqlException e) {
            throw new ResourceException(e.getMessage(), e);
        }
    }

    @Override
    protected EventConfig doGet(@Nonnull final Map parameterMap) {
        return doGetById(parameterMap, Plan.class, EventPlanConfig.createEmpty());
    }

    @Override
    protected EventPlanConfig createFrom(@Nonnull final Entity entity) {
        EventPlanConfig result = new EventPlanConfig();

        if (entity instanceof Plan) {
            Plan plan = (Plan) entity;

            result.setName(plan.getName());
            result.setDescription(plan.getDescription());
            result.setTime(plan.getTimeToComplete());
            String[] relatedDomainNames = ENTITY_NAME_EXTRACTOR.getNames(plan.getRelatedDomains());
            result.setDomains(TEXT_UTILS.getJoined(relatedDomainNames, ';'));
        }
        return result;
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static final class EventPlanConfig implements EventConfig {
        @XmlElement
        private String name;
        @XmlElement
        private String description;
        @XmlElement
        private String time;
        @XmlElement
        private String domains;

        public EventPlanConfig() {
            setName("");
            setDescription("");
            setTime("");
            setDomains("");
        }

        public static EventPlanConfig createEmpty() {
            return new EventPlanConfig();
        }

        @Override
        public boolean isFullfilled() {
            return StringUtils.isNotBlank(getName())
                    && getDescription() != null
                    && StringUtils.isNotBlank(getTime())
                    && StringUtils.isNotBlank(getDomains());
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

        public String getTime() {
            return time;
        }

        public void setTime(@Nonnull String time) {
            this.time = time;
        }

        public String getDomains() {
            return domains;
        }

        public void setDomains(@Nonnull String domains) {
            this.domains = domains;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EventPlanConfig that = (EventPlanConfig) o;

            if (!getName().equals(that.getName())) return false;
            if (getDescription() != null ? !getDescription().equals(that.getDescription()) : that.getDescription() != null)
                return false;
            if (!getTime().equals(that.getTime())) return false;
            return getDomains().equals(that.getDomains());

        }

        @Override
        public int hashCode() {
            return getName().hashCode();
        }
    }
}
