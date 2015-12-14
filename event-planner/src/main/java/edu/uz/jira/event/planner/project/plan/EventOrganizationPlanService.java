package edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import edu.uz.jira.event.planner.project.plan.model.*;
import edu.uz.jira.event.planner.project.plan.rest.EventDomainRestManager;
import edu.uz.jira.event.planner.project.plan.rest.EventPlanRestManager;
import net.java.ao.Query;
import net.java.ao.RawEntity;

import javax.annotation.Nonnull;
import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Service for transactional managing Event Organization Plan entities.
 * <ul>Best practices for developing with Active Objects (from Atlassian):</ul>
 * <li>When reading a large amount of data, do not use the 'find' of 'get' methods. Instead, use the 'stream' methods.</li>
 * <li>When deleting object graphs, or data from related tables, you will need to delete them in the right order.
 * Make sure you delete the children before the parents.
 * AO does not support cascading.</li>
 */
@Transactional
public class EventOrganizationPlanService {
    private final ActiveObjects activeObjectsService;

    public EventOrganizationPlanService(@Nonnull final ActiveObjects activeObjectsService) {
        this.activeObjectsService = activeObjectsService;
    }

    private PlanToTaskRelation associateEventTaskWithPlan(@Nonnull final Task task, @Nonnull final Plan plan) {
        PlanToTaskRelation postToLabel = activeObjectsService.create(PlanToTaskRelation.class);
        postToLabel.setTask(task);
        postToLabel.setPlan(plan);
        postToLabel.save();
        return postToLabel;
    }

    private PlanToDomainRelation associatePlanWithDomain(@Nonnull final Plan plan, @Nonnull final String domainName) {
        Domain[] domains = null;

        if (domainName != null) {
            domains = activeObjectsService.find(Domain.class, Query.select().where("NAME = ?", domainName));
        }
        if (domains != null && domains.length == 1 && plan != null && domains[0] != null) {
            return associateEventPlanWithDomain(plan, domains[0]);
        }
        return null;
    }

    private PlanToDomainRelation associateEventPlanWithDomain(@Nonnull final Plan plan, @Nonnull final Domain domain) {
        PlanToDomainRelation result = activeObjectsService.create(PlanToDomainRelation.class);
        result.setDomain(domain);
        result.setPlan(plan);
        result.save();
        return result;
    }

    /**
     * @param resource Configuration of Event Organization Plan.
     * @return Newly created and added to database Event Organization Plan.
     */
    public Plan addFrom(@Nonnull final EventPlanRestManager.EventPlanConfig resource) {
        if (resource == null) {
            return null;
        }
        Plan result = activeObjectsService.create(Plan.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.setTimeToComplete(resource.getTime());
        //associatePlanWithDomain(result, resource.getDomains());
        result.save();
        return result;
    }

    /**
     * @param resource Configuration of Event Organization Domain.
     * @return Newly created and added to database Event Organization Domain.
     */
    public Domain addFrom(@Nonnull final EventDomainRestManager.EventDomainConfig resource) {
        if (resource == null) {
            return null;
        }
        Domain result = activeObjectsService.create(Domain.class);

        result.setName(resource.getName());
        result.setDescription(resource.getDescription());

        result.save();
        return result;
    }

    /**
     * @return Database objects of specified type.
     */
    public <T extends RawEntity<K>, K> List<T> get(@Nonnull final Class<T> type) {
        if (type == null) {
            return new ArrayList<T>();
        }
        return newArrayList(activeObjectsService.find(type));
    }

    /**
     * @return Event Plans names (map values) sorted by Event Domains names (map keys).
     */
    public Map<String, List<String>> getEventPlansSortedByDomain() {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        for (Domain eachDomain : get(Domain.class)) {
            List<Plan> plans = Arrays.asList(eachDomain.getRelatedPlans());
            List<String> plansNames = new ArrayList<String>(plans.size());

            for (Plan eachPlan : plans) {
                plansNames.add(eachPlan.getName());
            }

            result.put(eachDomain.getName(), plansNames);
        }

        return result;
    }

    /**
     * Deletes all database content related with this plug-in.
     */
    public void clearDatabase() {
        activeObjectsService.delete();
        activeObjectsService.delete(activeObjectsService.find(Domain.class));
        activeObjectsService.delete(activeObjectsService.find(Task.class));
        activeObjectsService.delete(activeObjectsService.find(SubTask.class));
        activeObjectsService.delete(activeObjectsService.find(Component.class));
        activeObjectsService.delete(activeObjectsService.find(PlanToDomainRelation.class));
        activeObjectsService.delete(activeObjectsService.find(PlanToTaskRelation.class));
    }

}
