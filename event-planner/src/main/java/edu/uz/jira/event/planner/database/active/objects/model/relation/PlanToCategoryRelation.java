package edu.uz.jira.event.planner.database.active.objects.model.relation;

import edu.uz.jira.event.planner.database.active.objects.model.Category;
import edu.uz.jira.event.planner.database.active.objects.model.Plan;
import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Relation between Event Organization Plan and Event Organization Task.
 */
@Table("PlanToDomain")
public interface PlanToCategoryRelation extends Entity {
    String PLAN = "PLAN";
    String CATEGORY = "CATEGORY";

    Plan getPlan();

    void setPlan(Plan plan);

    Category getCategory();

    void setCategory(Category category);
}
