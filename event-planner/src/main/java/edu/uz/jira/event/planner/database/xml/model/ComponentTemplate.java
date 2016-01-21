package edu.uz.jira.event.planner.database.xml.model;

import edu.uz.jira.event.planner.project.plan.rest.ActiveObjectWrapper;
import edu.uz.jira.event.planner.util.text.EntityNameExtractor;
import net.java.ao.Entity;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * XML representation of Event Plan Component Template.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "task",
        "tasksNames"
})
public class ComponentTemplate implements ActiveObjectWrapper {
    @XmlElement(required = true)
    private List<TaskTemplate> task;
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "description")
    private String description;
    @XmlElement
    private String[] tasksNames;

    /**
     * @return Event Component Configuration with all empty fields (but not null).
     */
    public static ComponentTemplate createEmpty() {
        return new ComponentTemplate();
    }

    /**
     * @see {@link ActiveObjectWrapper#fill(Entity)}
     */
    @Override
    public ComponentTemplate fill(@Nonnull final Entity entity) {
        if (entity instanceof edu.uz.jira.event.planner.database.active.objects.model.Component) {
            edu.uz.jira.event.planner.database.active.objects.model.Component component = (edu.uz.jira.event.planner.database.active.objects.model.Component) entity;
            setName(component.getName());
            setDescription(component.getDescription());
            setTasksNames(EntityNameExtractor.getNames(component.getTasks()));
        }
        return this;
    }

    /**
     * @see {@link ActiveObjectWrapper#getWrappedType()}
     */
    @Override
    public Class<? extends RawEntity> getWrappedType() {
        return edu.uz.jira.event.planner.database.active.objects.model.Component.class;
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
        return new ComponentTemplate();
    }

    public String[] getTasksNames() {
        if (tasksNames == null && task != null) {
            tasksNames = new String[task.size()];
            for (int index = 0; index < task.size(); index++) {
                tasksNames[index] = task.get(index).getName();
            }
        }
        return tasksNames;
    }

    public void setTasksNames(String[] tasksNames) {
        this.tasksNames = tasksNames;
    }

    public List<TaskTemplate> getTask() {
        if (task == null) {
            task = new ArrayList<TaskTemplate>();
        }
        return this.task;
    }

    public void setTask(List<TaskTemplate> taskTemplate) {
        this.task = taskTemplate;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentTemplate componentTemplate = (ComponentTemplate) o;

        if (getTask() != null ? !getTask().equals(componentTemplate.getTask()) : componentTemplate.getTask() != null)
            return false;
        if (getName() != null ? !getName().equals(componentTemplate.getName()) : componentTemplate.getName() != null)
            return false;
        if (getDescription() != null ? !getDescription().equals(componentTemplate.getDescription()) : componentTemplate.getDescription() != null)
            return false;
        return Arrays.equals(getTasksNames(), componentTemplate.getTasksNames());
    }

    @Override
    public int hashCode() {
        int result = getTask() != null ? getTask().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + Arrays.hashCode(getTasksNames());
        return result;
    }
}
