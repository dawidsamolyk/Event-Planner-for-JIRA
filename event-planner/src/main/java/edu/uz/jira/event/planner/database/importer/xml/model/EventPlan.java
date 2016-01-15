package edu.uz.jira.event.planner.database.importer.xml.model;

import edu.uz.jira.event.planner.database.active.objects.model.Plan;
import edu.uz.jira.event.planner.project.plan.rest.ActiveObjectWrapper;
import edu.uz.jira.event.planner.util.text.EntityNameExtractor;
import edu.uz.jira.event.planner.util.text.TextUtils;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * XML representation of Event Plan.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "domain",
        "component",
        "id",
        "domainsNames",
        "componentsNames"
})
public class EventPlan implements ActiveObjectWrapper {
    @XmlElement(required = true)
    private List<Domain> domain;
    @XmlElement(required = true)
    private List<Component> component;
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "description")
    private String description;
    @XmlAttribute(name = "neededMonths", required = true)
    private int neededMonths;
    @XmlAttribute(name = "neededDays", required = true)
    private int neededDays;
    @XmlElement
    private int id;
    @XmlElement
    private String[] domainsNames;
    @XmlElement
    private String[] componentsNames;

    /**
     * @return Event Plan Configuration with all empty fields (but not null).
     */
    public static EventPlan createEmpty() {
        return new EventPlan();
    }

    /**
     * @see {@link ActiveObjectWrapper#fill(net.java.ao.Entity)}
     */
    @Override
    public ActiveObjectWrapper fill(@Nonnull final net.java.ao.Entity entity) {
        if (entity instanceof Plan) {
            Plan plan = (Plan) entity;
            setId(plan.getID());
            setName(plan.getName());
            setDescription(plan.getDescription());
            setNeededMonths(plan.getNeededMonthsToComplete());
            setNeededDays(plan.getNeededDaysToComplete());
            setDomainsNames(EntityNameExtractor.getNames(plan.getDomains()));
            setComponentsNames(EntityNameExtractor.getNames(plan.getComponents()));
        }
        return this;
    }

    /**
     * @see {@link ActiveObjectWrapper#getWrappedType()}
     */
    @Override
    public Class<? extends RawEntity> getWrappedType() {
        return Plan.class;
    }

    /**
     * @see {@link ActiveObjectWrapper#isFullfilled()}
     */
    @Override
    public boolean isFullfilled() {
        return StringUtils.isNotBlank(getName())
                && TextUtils.isEachElementNotBlank(getDomainsNames())
                && TextUtils.isEachElementNotBlank(getComponentsNames())
                && (getNeededMonths() > 0) || (getNeededMonths() == 0 && getNeededDays() > 0);
    }

    /**
     * @see {@link ActiveObjectWrapper#getEmptyCopy()}
     */
    @Override
    public ActiveObjectWrapper getEmptyCopy() {
        return createEmpty();
    }

    public List<Domain> getDomain() {
        if (domain == null) {
            domain = new ArrayList<Domain>();
        }
        return this.domain;
    }

    public void setDomain(List<Domain> domain) {
        this.domain = domain;
    }

    public List<Component> getComponent() {
        if (component == null) {
            component = new ArrayList<Component>();
        }
        return this.component;
    }

    public void setComponent(List<Component> component) {
        this.component = component;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getDescription() {
        if (description == null) {
            description = "";
        }
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public int getNeededMonths() {
        return neededMonths;
    }

    public void setNeededMonths(int value) {
        this.neededMonths = value;
    }

    public int getNeededDays() {
        return neededDays;
    }

    public void setNeededDays(int value) {
        this.neededDays = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getDomainsNames() {
        if (domainsNames == null && domain != null) {
            domainsNames = new String[domain.size()];
            for (int index = 0; index < domain.size(); index++) {
                domainsNames[index] = domain.get(index).getName();
            }
        }
        return domainsNames;
    }

    public void setDomainsNames(String[] domainsNames) {
        this.domainsNames = domainsNames;
    }

    public String[] getComponentsNames() {
        if (componentsNames == null && component != null) {
            componentsNames = new String[component.size()];
            for (int index = 0; index < component.size(); index++) {
                componentsNames[index] = component.get(index).getName();
            }
        }
        return componentsNames;
    }

    public void setComponentsNames(String[] componentsNames) {
        this.componentsNames = componentsNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventPlan eventPlan = (EventPlan) o;

        if (getNeededMonths() != eventPlan.getNeededMonths()) return false;
        if (getNeededDays() != eventPlan.getNeededDays()) return false;
        if (getId() != eventPlan.getId()) return false;
        if (getDomain() != null ? !getDomain().equals(eventPlan.getDomain()) : eventPlan.getDomain() != null)
            return false;
        if (getComponent() != null ? !getComponent().equals(eventPlan.getComponent()) : eventPlan.getComponent() != null)
            return false;
        if (getName() != null ? !getName().equals(eventPlan.getName()) : eventPlan.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(eventPlan.getDescription()) : eventPlan.getDescription() != null)
            return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(getDomainsNames(), eventPlan.getDomainsNames())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(getComponentsNames(), eventPlan.getComponentsNames());
    }

    @Override
    public int hashCode() {
        int result = getDomain() != null ? getDomain().hashCode() : 0;
        result = 31 * result + (getComponent() != null ? getComponent().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + getNeededMonths();
        result = 31 * result + getNeededDays();
        result = 31 * result + getId();
        result = 31 * result + Arrays.hashCode(getDomainsNames());
        result = 31 * result + Arrays.hashCode(getComponentsNames());
        return result;
    }
}
