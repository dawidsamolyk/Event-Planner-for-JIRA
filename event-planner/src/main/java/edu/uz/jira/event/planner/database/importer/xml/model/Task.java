package edu.uz.jira.event.planner.database.importer.xml.model;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * XML representation of Event Plan Task.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "subTask"
})
public class Task {
    @XmlElement(name = "sub-task")
    protected List<SubTask> subTask;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlAttribute(name = "neededMonths", required = true)
    protected byte neededMonths;
    @XmlAttribute(name = "neededDays", required = true)
    protected byte neededDays;

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
        return description;
    }

    public void setDescription(String value) {
        this.description = value;
    }

    public byte getNeededMonths() {
        return neededMonths;
    }

    public void setNeededMonths(byte value) {
        this.neededMonths = value;
    }

    public byte getNeededDays() {
        return neededDays;
    }

    public void setNeededDays(byte value) {
        this.neededDays = value;
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
        return !(getDescription() != null ? !getDescription().equals(task.getDescription()) : task.getDescription() != null);
    }

    @Override
    public int hashCode() {
        int result = getSubTask() != null ? getSubTask().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (int) getNeededMonths();
        result = 31 * result + (int) getNeededDays();
        return result;
    }
}
