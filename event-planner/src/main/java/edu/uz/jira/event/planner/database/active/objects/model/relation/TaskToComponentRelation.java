package edu.uz.jira.event.planner.database.active.objects.model.relation;

import edu.uz.jira.event.planner.database.active.objects.model.Component;
import edu.uz.jira.event.planner.database.active.objects.model.Task;
import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Relation between Event Organization Task and Event Organization Component.
 */
@Table("TaskToComponent")
public interface TaskToComponentRelation extends Entity {

    Task getTask();

    void setTask(Task task);

    Component getComponent();

    void setComponent(Component component);
}
