package edu.uz.jira.event.planner.database.active.objects.model;

/**
 * Interface of named database entity with description.
 */
public interface NamedEntityWithDescription extends NamedEntity {

    String getDescription();

    void setDescription(String description);
}
