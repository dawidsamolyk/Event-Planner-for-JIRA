package edu.uz.jira.event.planner.project.plan.rest;

import net.java.ao.Entity;

/**
 * Wrapper of Event Organziation element.
 */
public interface ActiveObjectWrapper {
    /**
     * @return Type of wrapped Active Object.
     */
    Class getWrappedType();

    /**
     * @param entity Source of data.
     * @return Fullfilled configuration from input source.
     */
    ActiveObjectWrapper fill(Entity entity);

    /**
     * @return Are all required parameters fullfilled.
     */
    boolean isFullfilled();

    /**
     * @return Copy of object without filled fields.
     */
    ActiveObjectWrapper getEmptyCopy();
}
