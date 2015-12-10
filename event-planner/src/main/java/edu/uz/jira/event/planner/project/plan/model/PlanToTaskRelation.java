package edu.uz.jira.event.planner.project.plan.model;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Relation between Event Organization Plan and Event Organization Task.
 */
@Table("EventPlanToTask")
public interface PlanToTaskRelation extends Entity {
    Plan getEventPlan();

    void setEventPlan(Plan plan);

    Task getEventTask();

    void setEventTask(Task task);
}
