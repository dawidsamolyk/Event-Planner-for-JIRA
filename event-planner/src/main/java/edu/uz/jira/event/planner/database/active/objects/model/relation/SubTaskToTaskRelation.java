package edu.uz.jira.event.planner.database.active.objects.model.relation;

import edu.uz.jira.event.planner.database.active.objects.model.SubTask;
import edu.uz.jira.event.planner.database.active.objects.model.Task;
import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Relation between Event Organization Task and Event Organization SubTask.
 */
@Table("SubTaskToTask")
public interface SubTaskToTaskRelation extends Entity {

    Task getTask();

    void setTask(Task task);

    SubTask getSubTask();

    void setSubTask(SubTask subTask);
}
