package edu.uz.jira.event.planner.database.importer.xml.model;

import javax.xml.bind.annotation.*;

/**
 * XML representation of Event Plan Domain.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "value"
})
public class Domain {
    @XmlValue
    protected String value;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "description")
    protected String description;

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

        Domain domain = (Domain) o;

        if (value != null ? !value.equals(domain.value) : domain.value != null) return false;
        if (getName() != null ? !getName().equals(domain.getName()) : domain.getName() != null) return false;
        return !(getDescription() != null ? !getDescription().equals(domain.getDescription()) : domain.getDescription() != null);
    }

    @Override
    public int hashCode() {
        int result = value != null ? value.hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        return result;
    }
}
