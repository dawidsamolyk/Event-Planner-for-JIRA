package edu.uz.jira.event.planner.database.xml.model;

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
 * XML representation of Event Plan Template.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "eventCategory",
        "component",
        "categoriesNames",
        "componentsNames"
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
    @XmlAttribute
    private int estimatedDaysToComplete;
    @XmlElement
    private String[] categoriesNames;
    @XmlElement
    private String[] componentsNames;

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
    public ActiveObjectWrapper fill(@Nonnull final net.java.ao.Entity entity) {
        if (entity instanceof Plan) {
            Plan plan = (Plan) entity;

            setId(plan.getID());
            setName(plan.getName());
            setDescription(plan.getDescription());
            setReserveTimeInDays(plan.getReserveTimeInDays());
            setEstimatedDaysToComplete(plan.getEstimatedDaysToComplete());
            setComponentsNames(EntityNameExtractor.getNames(plan.getComponents()));
            setCategoriesNames(EntityNameExtractor.getNames(plan.getCategories()));
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
                && TextUtils.isEachElementNotBlank(getCategoriesNames())
                && TextUtils.isEachElementNotBlank(getComponentsNames());
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

    public String[] getCategoriesNames() {
        if (categoriesNames == null && eventCategory != null) {
            categoriesNames = new String[eventCategory.size()];
            for (int index = 0; index < eventCategory.size(); index++) {
                categoriesNames[index] = eventCategory.get(index).getName();
            }
        }
        return categoriesNames;
    }

    public void setCategoriesNames(String[] categoriesNames) {
        this.categoriesNames = categoriesNames;
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

    public void setReserveTimeInDays(int reserveTimeInDays) {
        this.reserveTimeInDays = reserveTimeInDays;
    }

    public int getEstimatedDaysToComplete() {
        return estimatedDaysToComplete;
    }

    public void setEstimatedDaysToComplete(int estimatedDaysToComplete) {
        this.estimatedDaysToComplete = estimatedDaysToComplete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlanTemplate that = (PlanTemplate) o;

        if (reserveTimeInDays != that.reserveTimeInDays) return false;
        if (id != that.id) return false;
        if (estimatedDaysToComplete != that.estimatedDaysToComplete) return false;
        if (eventCategory != null ? !eventCategory.equals(that.eventCategory) : that.eventCategory != null)
            return false;
        if (component != null ? !component.equals(that.component) : that.component != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(categoriesNames, that.categoriesNames)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(componentsNames, that.componentsNames);
    }

    @Override
    public int hashCode() {
        int result = eventCategory != null ? eventCategory.hashCode() : 0;
        result = 31 * result + (component != null ? component.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + reserveTimeInDays;
        result = 31 * result + id;
        result = 31 * result + Arrays.hashCode(categoriesNames);
        result = 31 * result + Arrays.hashCode(componentsNames);
        result = 31 * result + estimatedDaysToComplete;
        return result;
    }
}
