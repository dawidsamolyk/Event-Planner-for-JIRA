package edu.uz.jira.event.planner.project.plan.model.relation;

import edu.uz.jira.event.planner.project.plan.model.Component;
import edu.uz.jira.event.planner.project.plan.model.Task;
import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Relation between Event Organization Task and Event Organization Component.
 */
@Table("TaskToComponent")
public interface TaskToComponentRelation extends Entity {
    String TASK = "PLAN";
    String COMPONENT = "COMPONENT";

    Task getTask();

    void setTask(Task task);

    Component getComponent();

    void setComponent(Component component);
}