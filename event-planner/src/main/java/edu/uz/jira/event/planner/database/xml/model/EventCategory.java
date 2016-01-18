package edu.uz.jira.event.planner.database.xml.model;

import edu.uz.jira.event.planner.database.active.objects.model.Category;
import edu.uz.jira.event.planner.project.plan.rest.ActiveObjectWrapper;
import net.java.ao.Entity;
import net.java.ao.RawEntity;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * XML representation of Event Plan EventCategory.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
public class EventCategory implements ActiveObjectWrapper {
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "description")
    private String description;

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

    /**
     * @return Event EventCategory Configuration with all empty fields (but not null).
     */
    public static EventCategory createEmpty() {
        return new EventCategory();
    }

    /**
     * @see {@link ActiveObjectWrapper#getEmptyCopy()}
     */
    public ActiveObjectWrapper getEmptyCopy() {
        return createEmpty();
    }

    /**
     * @see {@link ActiveObjectWrapper#fill(Entity)}
     */
    @Override
    public ActiveObjectWrapper fill(@Nonnull final Entity entity) {
        if (entity instanceof Category) {
            Category category = (Category) entity;
            setName(category.getName());
            setDescription(category.getDescription());
        }
        return this;
    }

    /**
     * @see {@link ActiveObjectWrapper#getWrappedType()}
     */
    @Override
    public Class<? extends RawEntity> getWrappedType() {
        return Category.class;
    }

    /**
     * @see {@link ActiveObjectWrapper#isFullfilled()}
     */
    @Override
    public boolean isFullfilled() {
        return StringUtils.isNotBlank(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventCategory eventCategory = (EventCategory) o;

        if (getName() != null ? !getName().equals(eventCategory.getName()) : eventCategory.getName() != null) return false;
        return !(getDescription() != null ? !getDescription().equals(eventCategory.getDescription()) : eventCategory.getDescription() != null);
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }
}
