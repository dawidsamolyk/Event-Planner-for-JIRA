package edu.uz.jira.event.planner.database.active.objects.model;

/**
 * Interface of named database entity.
 */
public interface NamedEntity {
    String NAME = "NAME";

    String getName();

    void setName(String name);
}
