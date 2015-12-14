package edu.uz.jira.event.planner.project.plan.model;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Interface of named database entity with description
 */
@Table("NamedEntity")
public interface NamedEntityWithDescription extends Entity {
    String NAME = "NAME";
    String DESCRIPTION = "DESCRIPTION";

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);
}
