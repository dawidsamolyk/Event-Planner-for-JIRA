package edu.uz.jira.event.planner.project.plan.model;

import net.java.ao.Entity;

/**
 * Interface of named database entity.
 */
public interface NamedEntity extends Entity {
    String NAME = "NAME";

    String getName();

    void setName(String name);
}
