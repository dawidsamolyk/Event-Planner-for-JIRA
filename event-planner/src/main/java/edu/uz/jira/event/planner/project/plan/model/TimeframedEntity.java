package edu.uz.jira.event.planner.project.plan.model;

import net.java.ao.Entity;

/**
 * Interface of time framed database entity.
 */
public interface TimeframedEntity extends Entity {
    String TIME_TO_COMPLETE = "TIME_TO_COMPLETE";

    String getTimeToComplete();

    void setTimeToComplete(String time);
}
