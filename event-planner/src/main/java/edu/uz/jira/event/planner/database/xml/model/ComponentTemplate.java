package edu.uz.jira.event.planner.database.xml.model;

import edu.uz.jira.event.planner.database.active.objects.model.Component;
import edu.uz.jira.event.planner.database.active.objects.model.Task;
import edu.uz.jira.event.planner.project.plan.rest.ActiveObjectWrapper;
import net.java.ao.Entity;
import net.java.ao.RawEntity;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * XML representation of Event Plan Component Template.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "task"
})
public class ComponentTemplate implements ActiveObjectWrapper {
    @XmlElement(required = true)
    private List<TaskTemplate> task;
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "description")
    private String description;
    @XmlAttribute
    private int id;

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
        if (entity instanceof Component) {
            Component component = (Component) entity;

            setId(component.getID());
            setName(component.getName());
            setDescription(component.getDescription());

            List<TaskTemplate> tasks = new ArrayList<TaskTemplate>();
            for (Task eachTask : component.getTasks()) {
                tasks.add(TaskTemplate.createEmpty().fill(eachTask));
            }
            setTask(tasks);
        }
        return this;
    }

    /**
     * @see {@link ActiveObjectWrapper#getWrappedType()}
     */
    @Override
    public Class<? extends RawEntity> getWrappedType() {
        return Component.class;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ComponentTemplate that = (ComponentTemplate) o;

        if (id != that.id) return false;
        if (task != null ? !task.equals(that.task) : that.task != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return description != null ? description.equals(that.description) : that.description == null;

    }

    @Override
    public int hashCode() {
        int result = task != null ? task.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + id;
        return result;
    }
}
