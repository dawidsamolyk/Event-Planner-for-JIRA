package edu.uz.jira.event.planner.database.active.objects.model.relation;

import edu.uz.jira.event.planner.database.active.objects.model.Component;
import edu.uz.jira.event.planner.database.active.objects.model.Plan;
import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Relation between Event Organization Plan and Event Organization Component.
 */
@Table("PlanToComponent")
public interface PlanToComponentRelation extends Entity {
    String PLAN = "PLAN";
    String COMPONENT = "COMPONENT";

    Plan getPlan();

    void setPlan(Plan plan);

    Component getComponent();

    void setComponent(Component component);
}
