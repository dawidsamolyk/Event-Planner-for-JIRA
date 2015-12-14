package edu.uz.jira.event.planner.project.plan.model;

import net.java.ao.ManyToMany;
import net.java.ao.schema.Table;

/**
 * Plan of the Event Organization.
 *
 * @Preload annotation tells Active Objects to load all the fields of the entity eagerly.
 * <ul>Best practices for developing with Active Objects (from Atlassian):</ul>
 * <li>The Active Objects framework does not know about renaming.
 * So if you change the name of an entity, it will remove the other entity and create a new one.
 * All the data in the entity will be lost.</li>
 * <li>When upgrading your plugin to a new version, do not remove columns unless you are aware of the consequences.
 * Active Objects will make the database match the entity interface in the Java code.
 * It alters tables to match the current interface. Removing columns will result in data loss.
 * For that reason, we recommend that you do not delete tables or columns, only add them.</li>
 * <li>When migrating from one data type to another, the recommended approach is not to use an in-place type conversion.
 * Instead, create a new column and migrate the data during the upgrade process.</li>
 * <li>If you need to specify the raw column names in create or find operations, letter case is important.</li>
 */
@Table("EventPlan")
public interface Plan extends NamedEntity {
    String DESCRIPTION = "DESCRIPTION";
    String TIME_TO_COMPLETE = "TIME_TO_COMPLETE";
    String RELATED_DOMAINS = "RELATED_DOMAINS";
    String RELATED_TASKS = "RELATED_TASKS";

    String getDescription();

    void setDescription(String description);

    String getTimeToComplete();

    void setTimeToComplete(String time);

    @ManyToMany(value = PlanToDomainRelation.class)
    Domain[] getRelatedDomains();

    @ManyToMany(value = PlanToTaskRelation.class)
    Task[] getRelatedTasks();
}
