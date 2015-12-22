package edu.uz.jira.event.planner.project.plan.model;

/**
 * Interface of time framed database entity.
 */
public interface TimeFramedEntity {
    String TIME_TO_COMPLETE = "TIME_TO_COMPLETE";

    long getTimeToComplete();

    void setTimeToComplete(long time);
}
