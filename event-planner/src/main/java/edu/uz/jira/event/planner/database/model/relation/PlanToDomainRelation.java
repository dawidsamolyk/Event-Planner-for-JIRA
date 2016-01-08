package edu.uz.jira.event.planner.database.model.relation;

import edu.uz.jira.event.planner.database.model.Domain;
import edu.uz.jira.event.planner.database.model.Plan;
import net.java.ao.Entity;
import net.java.ao.schema.Table;

/**
 * Relation between Event Organization Plan and Event Organization Task.
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
