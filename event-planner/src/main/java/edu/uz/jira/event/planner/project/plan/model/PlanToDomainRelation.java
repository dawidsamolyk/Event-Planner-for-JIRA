package edu.uz.jira.event.planner.project.plan.model;

import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Relation between Event Organization Plan and Event Organization Task.
 * @Preload annotation tells Active Objects to load all the fields of the entity eagerly.
 */
@Table("PlanToDomain")
public interface PlanToDomainRelation extends Entity {
    String PLAN = "PLAN";
    String DOMAIN = "DOMAIN";

    Plan getPlan();

    void setPlan(Plan plan);

    Domain getDomain();

    void setDomain(Domain domain);
}
