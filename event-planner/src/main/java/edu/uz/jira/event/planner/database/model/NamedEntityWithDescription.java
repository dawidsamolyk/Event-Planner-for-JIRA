package edu.uz.jira.event.planner.database.model;

/**
 * Interface of named database entity with description.
 */
public interface NamedEntityWithDescription {
    String NAME = "NAME";
    String DESCRIPTION = "DESCRIPTION";

    String getName();

    void setName(String name);

    String getDescription();

    void setDescription(String description);
}
