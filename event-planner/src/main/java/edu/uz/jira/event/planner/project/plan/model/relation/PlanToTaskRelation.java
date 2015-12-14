package edu.uz.jira.event.planner.project.plan.model.relation;

import edu.uz.jira.event.planner.project.plan.model.Plan;
import edu.uz.jira.event.planner.project.plan.model.Task;
import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Relation between Event Organization Plan and Event Organization Task.
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
