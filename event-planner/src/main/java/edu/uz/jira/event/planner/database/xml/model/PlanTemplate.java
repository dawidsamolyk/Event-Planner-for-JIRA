package edu.uz.jira.event.planner.database.xml.model;

import edu.uz.jira.event.planner.database.active.objects.model.Category;
import edu.uz.jira.event.planner.database.active.objects.model.Component;
import edu.uz.jira.event.planner.database.active.objects.model.Plan;
import edu.uz.jira.event.planner.project.plan.rest.ActiveObjectWrapper;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * XML representation of Event Plan Template.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "eventCategory",
        "component"
})
public class PlanTemplate implements ActiveObjectWrapper {
    @XmlElement(required = true)
    private List<EventCategory> eventCategory;
    @XmlElement(required = true)
    private List<ComponentTemplate> component;
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "description")
    private String description;
    @XmlAttribute(name = "reserveTimeInDays")
    private int reserveTimeInDays;
    @XmlAttribute
    private int id;

    /**
     * @return Event Plan Configuration with all empty fields (but not null).
     */
    public static PlanTemplate createEmpty() {
        return new PlanTemplate();
    }

    /**
     * @see {@link ActiveObjectWrapper#fill(net.java.ao.Entity)}
     */
    @Override
    public PlanTemplate fill(@Nonnull final net.java.ao.Entity entity) {
        if (entity instanceof Plan) {
            Plan plan = (Plan) entity;

            setId(plan.getID());
            setName(plan.getName());
            setDescription(plan.getDescription());
            setReserveTimeInDays(plan.getReserveTimeInDays());

            List<ComponentTemplate> components = new ArrayList<ComponentTemplate>();
            for (Component eachComponent : plan.getComponents()) {
                components.add(ComponentTemplate.createEmpty().fill(eachComponent));
            }
            setComponent(components);

            List<EventCategory> categories = new ArrayList<EventCategory>();
            for (Category eachCategory : plan.getCategories()) {
                categories.add(EventCategory.createEmpty().fill(eachCategory));
            }
            setEventCategory(categories);
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
        return StringUtils.isNotBlank(getName());
    }

    /**
     * @see {@link ActiveObjectWrapper#getEmptyCopy()}
     */
    @Override
    public ActiveObjectWrapper getEmptyCopy() {
        return createEmpty();
    }

    public List<EventCategory> getEventCategory() {
        if (eventCategory == null) {
            eventCategory = new ArrayList<EventCategory>();
        }
        return this.eventCategory;
    }

    public void setEventCategory(List<EventCategory> eventCategory) {
        this.eventCategory = eventCategory;
    }

    public List<ComponentTemplate> getComponent() {
        if (component == null) {
            component = new ArrayList<ComponentTemplate>();
        }
        return this.component;
    }

    public void setComponent(List<ComponentTemplate> component) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReserveTimeInDays() {
        return reserveTimeInDays;
    }

    public void setReserveTimeInDays(int reserveTimeInDays) {
        this.reserveTimeInDays = reserveTimeInDays;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanTemplate that = (PlanTemplate) o;

        if (reserveTimeInDays != that.reserveTimeInDays) return false;
        if (id != that.id) return false;
        if (eventCategory != null ? !eventCategory.equals(that.eventCategory) : that.eventCategory != null)
            return false;
        if (component != null ? !component.equals(that.component) : that.component != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return description != null ? description.equals(that.description) : that.description == null;

    }

    @Override
    public int hashCode() {
        int result = eventCategory != null ? eventCategory.hashCode() : 0;
        result = 31 * result + (component != null ? component.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + reserveTimeInDays;
        result = 31 * result + id;
        return result;
    }
}
