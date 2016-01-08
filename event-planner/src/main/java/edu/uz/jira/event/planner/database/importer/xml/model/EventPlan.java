package edu.uz.jira.event.planner.database.importer.xml.model;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * XML representation of Event Plan.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "domain",
        "component"
})
public class EventPlan {
    @XmlElement(required = true)
    protected List<Domain> domain;
    @XmlElement(required = true)
    protected List<Component> component;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "description")
    protected String description;
    @XmlAttribute(name = "neededMonths", required = true)
    protected byte neededMonths;
    @XmlAttribute(name = "neededDays", required = true)
    protected byte neededDays;

    public List<Domain> getDomain() {
        if (domain == null) {
            domain = new ArrayList<Domain>();
        }
        return this.domain;
    }

    public void setDomain(List<Domain> domain) {
        this.domain = domain;
    }

    public List<Component> getComponent() {
        if (component == null) {
            component = new ArrayList<Component>();
        }
        return this.component;
    }

    public void setComponent(List<Component> component) {
        this.component = component;
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

        EventPlan eventPlan = (EventPlan) o;

        if (getNeededMonths() != eventPlan.getNeededMonths()) return false;
        if (getNeededDays() != eventPlan.getNeededDays()) return false;
        if (getDomain() != null ? !getDomain().equals(eventPlan.getDomain()) : eventPlan.getDomain() != null)
            return false;
        if (getComponent() != null ? !getComponent().equals(eventPlan.getComponent()) : eventPlan.getComponent() != null)
            return false;
        if (getName() != null ? !getName().equals(eventPlan.getName()) : eventPlan.getName() != null) return false;
        return !(getDescription() != null ? !getDescription().equals(eventPlan.getDescription()) : eventPlan.getDescription() != null);
    }

    @Override
    public int hashCode() {
        int result = getDomain() != null ? getDomain().hashCode() : 0;
        result = 31 * result + (getComponent() != null ? getComponent().hashCode() : 0);
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (int) getNeededMonths();
        result = 31 * result + (int) getNeededDays();
        return result;
    }
}
