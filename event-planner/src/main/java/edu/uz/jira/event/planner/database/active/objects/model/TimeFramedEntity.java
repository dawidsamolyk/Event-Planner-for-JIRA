package edu.uz.jira.event.planner.database.active.objects.model;

/**
 * Interface of time framed database entity.
 */
public interface TimeFramedEntity {
    int getNeededMonthsToComplete();

    void setNeededMonthsToComplete(int numberOfMonths);

    int getNeededDaysToComplete();

    void setNeededDaysToComplete(int numberOfDays);
}
