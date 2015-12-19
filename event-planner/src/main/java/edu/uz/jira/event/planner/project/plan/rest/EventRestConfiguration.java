package edu.uz.jira.event.planner.project.plan.rest;

import net.java.ao.Entity;

/**
 * Event Organziation element REST configuration.
 */
public interface EventRestConfiguration {
    /**
     * @return Type of wrapped object.
     */
    Class getWrappedType();

    /**
     * @param entity Source of data.
     * @return Fullfilled configuration from input source.
     */
    EventRestConfiguration fill(Entity entity);

    /**
     * @return Are all required parameters fullfilled.
     */
    boolean isFullfilled();
}
