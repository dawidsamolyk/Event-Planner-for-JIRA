package edu.uz.jira.event.planner.database.importer.xml.model;

import edu.uz.jira.event.planner.project.plan.rest.ActiveObjectWrapper;
import edu.uz.jira.event.planner.util.text.EntityNameExtractor;
import edu.uz.jira.event.planner.util.text.TextUtils;
import net.java.ao.Entity;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * XML representation of Event Plan Component.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "task",
        "tasksNames"
})
public class Component implements ActiveObjectWrapper {
    @XmlElement(required = true)
    protected List<Task> task;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlElement
    private String[] tasksNames;

    /**
     * @return Event Component Configuration with all empty fields (but not null).
     */
    public static Component createEmpty() {
        return new Component();
    }

    /**
     * @see {@link ActiveObjectWrapper#fill(Entity)}
     */
    @Override
    public Component fill(@Nonnull final Entity entity) {
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
    public Class getWrappedType() {
        return edu.uz.jira.event.planner.database.active.objects.model.Component.class;
    }

    /**
     * @see {@link ActiveObjectWrapper#isFullfilled()}
     */
    @Override
    public boolean isFullfilled() {
        return StringUtils.isNotBlank(getName()) && TextUtils.isEachElementNotBlank(tasksNames);
    }

    /**
     * @see {@link ActiveObjectWrapper#getEmptyCopy()}
     */
    @Override
    public ActiveObjectWrapper getEmptyCopy() {
        return new Component();
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

    public List<Task> getTask() {
        if (task == null) {
            task = new ArrayList<Task>();
        }
        return this.task;
    }

    public void setTask(List<Task> task) {
        this.task = task;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Component component = (Component) o;

        if (getTask() != null ? !getTask().equals(component.getTask()) : component.getTask() != null) return false;
        if (getName() != null ? !getName().equals(component.getName()) : component.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(component.getDescription()) : component.getDescription() != null)
            return false;
        return Arrays.equals(getTasksNames(), component.getTasksNames());

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
