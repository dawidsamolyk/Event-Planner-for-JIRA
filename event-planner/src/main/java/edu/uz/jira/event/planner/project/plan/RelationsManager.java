package edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.project.plan.model.Domain;
import edu.uz.jira.event.planner.project.plan.model.Plan;
import edu.uz.jira.event.planner.project.plan.model.Task;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToTaskRelation;
import net.java.ao.Query;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Manages database Entities (Active Objects) relations.
 */
class RelationsManager {
    private final ActiveObjects activeObjectsService;

    /**
     * Constructor.
     *
     * @param activeObjectsService Injected {@code ActiveObjects} implementation.
     */
    RelationsManager(@Nonnull final ActiveObjects activeObjectsService) {
        this.activeObjectsService = activeObjectsService;
    }

    PlanToTaskRelation associateEventTaskWithPlan(@Nonnull final Task task, @Nonnull final Plan plan) {
        PlanToTaskRelation postToLabel = activeObjectsService.create(PlanToTaskRelation.class);
        postToLabel.setTask(task);
        postToLabel.setPlan(plan);
        postToLabel.save();
        return postToLabel;
    }

    Collection<PlanToDomainRelation> associatePlanWithDomains(@Nonnull final Plan plan, @Nonnull final String[] domainsNames) {
        Collection<PlanToDomainRelation> result = new ArrayList<PlanToDomainRelation>();

        List<Domain> domains = new ArrayList<Domain>();
        if (domainsNames != null && domainsNames.length > 0) {
            for (String eachDomainName : domainsNames) {
                Domain[] eachDomains = activeObjectsService.find(Domain.class, Query.select().where(Domain.NAME + " = ?", eachDomainName));
                domains.addAll(Arrays.asList(eachDomains));
            }
        }
        if (domains != null && domains.size() > 0 && plan != null) {
            for (Domain eachDomain : domains) {
                PlanToDomainRelation eachRelation = associatePlanWithDomain(plan, eachDomain);
                result.add(eachRelation);
            }
        }
        return result;
    }

    PlanToDomainRelation associatePlanWithDomain(@Nonnull final Plan plan, @Nonnull final Domain domain) {
        PlanToDomainRelation result = activeObjectsService.create(PlanToDomainRelation.class);
        result.setDomain(domain);
        result.setPlan(plan);
        result.save();
        return result;
    }
}
