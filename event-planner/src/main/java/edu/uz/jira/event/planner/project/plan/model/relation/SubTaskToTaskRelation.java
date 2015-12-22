package edu.uz.jira.event.planner.project.plan.model.relation;

import edu.uz.jira.event.planner.project.plan.model.SubTask;
import edu.uz.jira.event.planner.project.plan.model.Task;
import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Relation between Event Organization Task and Event Organization SubTask.
 */
@Table("SubTaskToTask")
public interface SubTaskToTaskRelation extends Entity {
    String TASK = "PLAN";
    String SUB_TASK = "SUB_TASK";

    Task getTask();

    void setTask(Task task);

    SubTask getSubTask();

    void setSubTask(SubTask subTask);
}
