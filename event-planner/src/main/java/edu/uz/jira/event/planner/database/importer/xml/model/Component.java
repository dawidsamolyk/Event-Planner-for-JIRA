package edu.uz.jira.event.planner.database.importer.xml.model;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * XML representation of Event Plan Component.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "task"
})
public class Component {
    @XmlElement(required = true)
    protected List<Task> task;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "description")
    protected String description;

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
        return !(getDescription() != null ? !getDescription().equals(component.getDescription()) : component.getDescription() != null);
    }

    @Override
    public int hashCode() {
        int result = getTask() != null ? getTask().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }
}
