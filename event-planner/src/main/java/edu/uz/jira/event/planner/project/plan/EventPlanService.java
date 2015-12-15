package edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import edu.uz.jira.event.planner.project.plan.model.*;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToTaskRelation;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventDomainRestManager;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventPlanRestManager;
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
public class EventPlanService {
    private final ActiveObjects activeObjectsService;
    private final RelationsManager relationsManager;

    /**
     * Constructor.
     *
     * @param activeObjectsService Injected {@code ActiveObjects} implementation.
     */
    public EventPlanService(@Nonnull final ActiveObjects activeObjectsService) {
        this.activeObjectsService = activeObjectsService;
        relationsManager = new RelationsManager(activeObjectsService);
    }

    /**
     * @param resource Configuration of Event Organization Plan.
     * @return Newly created and added to database Event Organization Plan.
     */
    public Plan addFrom(@Nonnull final EventPlanRestManager.EventPlanConfig resource) {
        if (resource == null || !resource.isFullfilled()) {
            return null;
        }
        Plan result = activeObjectsService.create(Plan.class);

        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.setTimeToComplete(resource.getTime());
        relationsManager.associatePlanWithDomains(result, resource.getDomains());
        relationsManager.associatePlanWithComponents(result, resource.getComponents());

        result.save();
        return result;
    }


    /**
     * @param resource Configuration of Event Organization Domain.
     * @return Newly created and added to database Event Organization Domain.
     */
    public Domain addFrom(@Nonnull final EventDomainRestManager.EventDomainConfig resource) {
        if (resource == null || !resource.isFullfilled()) {
            return null;
        }
        Domain result = activeObjectsService.create(Domain.class);

        result.setName(resource.getName());
        result.setDescription(resource.getDescription());

        result.save();
        return result;
    }

    /**
     * @param type Type of entities to get.
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
            List<Plan> plans = Arrays.asList(eachDomain.getPlans());
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
        deleteAll(PlanToDomainRelation.class);
        deleteAll(PlanToTaskRelation.class);
        deleteAll(SubTask.class);
        deleteAll(Task.class);
        deleteAll(Component.class);
        deleteAll(Domain.class);
        deleteAll(Plan.class);
    }

    /**
     * @param type Type of entities to delete.
     */
    public void deleteAll(@Nonnull final Class<? extends RawEntity> type) {
        activeObjectsService.delete(activeObjectsService.find(type));
    }
}
