package edu.uz.jira.event.planner.database.importer.xml.model;

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
 * XML representation of Event Plan Task.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "subTask",
        "subTasksNames"
})
public class Task implements ActiveObjectWrapper {
    @XmlElement(name = "sub-task")
    private List<SubTask> subTask;
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlAttribute(name = "description")
    private String description;
    @XmlAttribute(name = "neededMonths", required = true)
    private int neededMonths;
    @XmlAttribute(name = "neededDays", required = true)
    private int neededDays;
    @XmlElement
    private String[] subTasksNames;

    /**
     * @return Event Task Configuration with all empty fields (but not null).
     */
    public static Task createEmpty() {
        return new Task();
    }

    /**
     * @see {@link ActiveObjectWrapper#fill(Entity)}
     */
    @Override
    public ActiveObjectWrapper fill(@Nonnull final Entity entity) {
        if (entity instanceof edu.uz.jira.event.planner.database.active.objects.model.Task) {
            edu.uz.jira.event.planner.database.active.objects.model.Task task = (edu.uz.jira.event.planner.database.active.objects.model.Task) entity;
            setName(task.getName());
            setDescription(task.getDescription());
            setNeededMonths(task.getNeededMonthsToComplete());
            setNeededDays(task.getNeededDaysToComplete());
            setSubTasksNames(EntityNameExtractor.getNames(task.getSubTasks()));
        }
        return this;
    }

    /**
     * @see {@link ActiveObjectWrapper#getWrappedType()}
     */
    @Override
    public Class<? extends RawEntity> getWrappedType() {
        return edu.uz.jira.event.planner.database.active.objects.model.Task.class;
    }

    /**
     * @see {@link ActiveObjectWrapper#isFullfilled()}
     */
    @Override
    public boolean isFullfilled() {
        return StringUtils.isNotBlank(getName())
                && (getNeededMonths() > 0) || (getNeededMonths() == 0 && getNeededDays() > 0);
    }

    /**
     * @see {@link ActiveObjectWrapper#getEmptyCopy()}
     */
    @Override
    public ActiveObjectWrapper getEmptyCopy() {
        return createEmpty();
    }

    public List<SubTask> getSubTask() {
        if (subTask == null) {
            subTask = new ArrayList<SubTask>();
        }
        return this.subTask;
    }

    public void setSubTask(List<SubTask> subTask) {
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

    public String[] getSubTasksNames() {
        if (subTasksNames == null && subTask != null) {
            subTasksNames = new String[subTask.size()];
            for (int index = 0; index < subTask.size(); index++) {
                subTasksNames[index] = subTask.get(index).getName();
            }
        }
        return subTasksNames;
    }

    public void setSubTasksNames(String[] subTasksNames) {
        this.subTasksNames = subTasksNames;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (getNeededMonths() != task.getNeededMonths()) return false;
        if (getNeededDays() != task.getNeededDays()) return false;
        if (getSubTask() != null ? !getSubTask().equals(task.getSubTask()) : task.getSubTask() != null) return false;
        if (getName() != null ? !getName().equals(task.getName()) : task.getName() != null) return false;
        if (getDescription() != null ? !getDescription().equals(task.getDescription()) : task.getDescription() != null)
            return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(getSubTasksNames(), task.getSubTasksNames());
    }

    @Override
    public int hashCode() {
        int result = getSubTask() != null ? getSubTask().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + getNeededMonths();
        result = 31 * result + getNeededDays();
        result = 31 * result + Arrays.hashCode(getSubTasksNames());
        return result;
    }
}
