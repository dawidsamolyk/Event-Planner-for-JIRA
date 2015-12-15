package edu.uz.jira.event.planner.project.plan.model;

/**
 * Interface of time framed database entity.
 */
public interface TimeframedEntity {
    String TIME_TO_COMPLETE = "TIME_TO_COMPLETE";

    String getTimeToComplete();

    void setTimeToComplete(String time);
}
