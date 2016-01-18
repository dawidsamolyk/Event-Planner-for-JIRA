package edu.uz.jira.event.planner.database.xml.model;

import edu.uz.jira.event.planner.project.plan.rest.ActiveObjectWrapper;
import net.java.ao.Entity;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * XML representation of Event Plan SubTask Template.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
public class SubTaskTemplate implements ActiveObjectWrapper {
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "description")
    private String description;

    /**
     * @return Event SubTask Configuration with all empty fields (but not null).
     */
    public static SubTaskTemplate createEmpty() {
        return new SubTaskTemplate();
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

    /**
     * @see {@link ActiveObjectWrapper#fill(Entity)}
     */
    @Override
    public ActiveObjectWrapper fill(@Nonnull final Entity entity) {
        if (entity instanceof edu.uz.jira.event.planner.database.active.objects.model.SubTask) {
            edu.uz.jira.event.planner.database.active.objects.model.SubTask subtask = (edu.uz.jira.event.planner.database.active.objects.model.SubTask) entity;
            setName(subtask.getName());
            setDescription(subtask.getDescription());
        }
        return this;
    }

    /**
     * @see {@link ActiveObjectWrapper#getWrappedType()}
     */
    @Override
    public Class<? extends RawEntity> getWrappedType() {
        return edu.uz.jira.event.planner.database.active.objects.model.SubTask.class;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubTaskTemplate subTaskTemplate = (SubTaskTemplate) o;

        if (getName() != null ? !getName().equals(subTaskTemplate.getName()) : subTaskTemplate.getName() != null) return false;
        return !(getDescription() != null ? !getDescription().equals(subTaskTemplate.getDescription()) : subTaskTemplate.getDescription() != null);
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }
}
