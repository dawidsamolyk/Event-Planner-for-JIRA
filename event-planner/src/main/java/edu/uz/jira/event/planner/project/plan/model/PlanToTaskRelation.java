package edu.uz.jira.event.planner.project.plan.model;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

import java.io.Serializable;

/**
 * Relation between Event Organization Plan and Event Organization Task.
 */
@Table("EventPlanToTask")
public interface PlanToTaskRelation extends Entity, Serializable {
    Plan getEventPlan();

    void setEventPlan(Plan plan);

    Task getEventTask();

    void setEventTask(Task task);
}
