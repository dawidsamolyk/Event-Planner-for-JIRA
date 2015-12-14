package edu.uz.jira.event.planner.project.plan.model;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Relation between Event Organization Plan and Event Organization Task.
 * @Preload annotation tells Active Objects to load all the fields of the entity eagerly.
 */
@Table("PlanToTask")
public interface PlanToTaskRelation extends Entity {
    String PLAN = "PLAN";
    String TASK = "TASK";

    Plan getPlan();

    void setPlan(Plan plan);

    Task getTask();

    void setTask(Task task);
}
