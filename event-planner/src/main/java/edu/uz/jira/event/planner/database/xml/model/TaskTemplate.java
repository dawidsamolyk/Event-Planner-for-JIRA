package edu.uz.jira.event.planner.database.xml.model;

import edu.uz.jira.event.planner.database.active.objects.model.SubTask;
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
 * XML representation of Event Plan Task Template.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "subTask"
})
public class TaskTemplate implements ActiveObjectWrapper {
    @XmlElement(name = "sub-task")
    private List<SubTaskTemplate> subTask;
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "description")
    private String description;
    @XmlAttribute(name = "neededMonthsBeforeEvent", required = true)
    private int neededMonthsBeforeEvent;
    @XmlAttribute(name = "neededDaysBeforeEvent", required = true)
    private int neededDaysBeforeEvent;
    @XmlAttribute
    private int id;

    /**
     * @return Event Task Configuration with all empty fields (but not null).
     */
    public static TaskTemplate createEmpty() {
        return new TaskTemplate();
    }

    /**
     * @see {@link ActiveObjectWrapper#fill(Entity)}
     */
    @Override
    public TaskTemplate fill(@Nonnull final Entity entity) {
        if (entity instanceof Task) {
            Task task = (Task) entity;

            setId(task.getID());
            setName(task.getName());
            setDescription(task.getDescription());
            setNeededMonthsBeforeEvent(task.getNeededMonthsToComplete());
            setNeededDaysBeforeEvent(task.getNeededDaysToComplete());

            List<SubTaskTemplate> tasks = new ArrayList<SubTaskTemplate>();
            for (SubTask eachSubTask : task.getSubTasks()) {
                tasks.add(SubTaskTemplate.createEmpty().fill(eachSubTask));
            }
            setSubTask(tasks);
        }
        return this;
    }

    /**
     * @see {@link ActiveObjectWrapper#getWrappedType()}
     */
    @Override
    public Class<? extends RawEntity> getWrappedType() {
        return Task.class;
    }

    /**
     * @see {@link ActiveObjectWrapper#isFullfilled()}
     */
    @Override
    public boolean isFullfilled() {
        return StringUtils.isNotBlank(getName())
                && (getNeededDaysBeforeEvent() > 0) || (getNeededMonthsBeforeEvent() == 0 && getNeededMonthsBeforeEvent() > 0);
    }

    /**
     * @see {@link ActiveObjectWrapper#getEmptyCopy()}
     */
    @Override
    public ActiveObjectWrapper getEmptyCopy() {
        return createEmpty();
    }

    public List<SubTaskTemplate> getSubTask() {
        if (subTask == null) {
            subTask = new ArrayList<SubTaskTemplate>();
        }
        return this.subTask;
    }

    public void setSubTask(List<SubTaskTemplate> subTask) {
        this.subTask = subTask;
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

    public int getNeededMonthsBeforeEvent() {
        return neededMonthsBeforeEvent;
    }

    public void setNeededMonthsBeforeEvent(int value) {
        this.neededMonthsBeforeEvent = value;
    }

    public int getNeededDaysBeforeEvent() {
        return neededDaysBeforeEvent;
    }

    public void setNeededDaysBeforeEvent(int value) {
        this.neededDaysBeforeEvent = value;
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

        TaskTemplate that = (TaskTemplate) o;

        if (neededMonthsBeforeEvent != that.neededMonthsBeforeEvent) return false;
        if (neededDaysBeforeEvent != that.neededDaysBeforeEvent) return false;
        if (id != that.id) return false;
        if (subTask != null ? !subTask.equals(that.subTask) : that.subTask != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return description != null ? description.equals(that.description) : that.description == null;

    }

    @Override
    public int hashCode() {
        int result = subTask != null ? subTask.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + neededMonthsBeforeEvent;
        result = 31 * result + neededDaysBeforeEvent;
        result = 31 * result + id;
        return result;
    }
}
