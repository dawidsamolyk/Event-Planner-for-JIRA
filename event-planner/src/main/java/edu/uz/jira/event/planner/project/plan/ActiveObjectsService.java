package edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import edu.uz.jira.event.planner.project.plan.model.*;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.project.plan.rest.EventRestConfiguration;
import edu.uz.jira.event.planner.project.plan.rest.manager.*;
import net.java.ao.Entity;
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
public class ActiveObjectsService {
    private final ActiveObjects activeObjectsService;
    private final RelationsManager relationsManager;

    /**
     * Constructor.
     *
     * @param activeObjectsService Injected {@code ActiveObjects} implementation.
     */
    public ActiveObjectsService(@Nonnull final ActiveObjects activeObjectsService) {
        this.activeObjectsService = activeObjectsService;
        relationsManager = new RelationsManager(activeObjectsService);
    }

    public Entity addFrom(@Nonnull final EventRestConfiguration resource) {
        if (resource instanceof EventPlanRestManager.Configuration) {
            return addFrom((EventPlanRestManager.Configuration) resource);
        }
        if (resource instanceof EventDomainRestManager.Configuration) {
            return addFrom((EventDomainRestManager.Configuration) resource);
        }
        if (resource instanceof EventComponentRestManager.Configuration) {
            return addFrom((EventComponentRestManager.Configuration) resource);
        }
        if (resource instanceof EventTaskRestManager.Configuration) {
            return addFrom((EventTaskRestManager.Configuration) resource);
        }
        if (resource instanceof EventSubTaskRestManager.Configuration) {
            return addFrom((EventSubTaskRestManager.Configuration) resource);
        }
        return null;
    }

    /**
     * @param resource Configuration of Event Organization Plan.
     * @return Newly created and added to database Event Organization Plan. Null if not created.
     */
    public Plan addFrom(@Nonnull final EventPlanRestManager.Configuration resource) {
        if (resource == null || !resource.isFullfilled()) {
            return null;
        }
        Plan result = activeObjectsService.create(Plan.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.setTimeToComplete(resource.getTime());

        Collection<PlanToDomainRelation> planToDomainRelations = relationsManager.associatePlanWithDomains(result, resource.getDomains());
        if (planToDomainRelations.isEmpty()) {
            activeObjectsService.delete(result);
            return null;
        }

        Collection<PlanToComponentRelation> planToComponentRelations = relationsManager.associatePlanWithComponents(result, resource.getComponents());
        if (planToComponentRelations.isEmpty()) {
            for (PlanToDomainRelation each : planToDomainRelations) {
                activeObjectsService.delete(each);
            }
            activeObjectsService.delete(result);
            return null;
        }

        result.save();
        return result;
    }

    /**
     * @param resource Configuration of Event Organization Domain.
     * @return Newly created and added to database Event Organization Domain.
     */
    public Domain addFrom(@Nonnull final EventDomainRestManager.Configuration resource) {
        if (resource == null || !resource.isFullfilled()) {
            return null;
        }
        Domain result = activeObjectsService.create(Domain.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.save();
        return result;
    }

    public Component addFrom(@Nonnull final EventComponentRestManager.Configuration resource) {
        if (resource == null || !resource.isFullfilled()) {
            return null;
        }
        Component result = activeObjectsService.create(Component.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());

        boolean valid = relationsManager.associate(result, resource.getTasks());
        if (!valid) {
            activeObjectsService.delete(result);
            return null;
        }

        result.save();
        return result;
    }

    public Task addFrom(EventTaskRestManager.Configuration resource) {
        if (resource == null || !resource.isFullfilled()) {
            return null;
        }
        Task result = activeObjectsService.create(Task.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.setTimeToComplete(resource.getTime());

        boolean valid = relationsManager.associate(result, resource.getSubtasks());
        if (!valid) {
            activeObjectsService.delete(result);
            return null;
        }

        result.save();
        return result;
    }

    public SubTask addFrom(EventSubTaskRestManager.Configuration resource) {
        if (resource == null || !resource.isFullfilled()) {
            return null;
        }
        SubTask result = activeObjectsService.create(SubTask.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.setTimeToComplete(resource.getTime());
        result.save();
        return result;
    }

    /**
     * @param type Type of entities to get.
     * @return Database objects of specified type.
     */
    public <T extends RawEntity<Integer>> List<T> get(@Nonnull final Class<T> type) {
        if (type == null) {
            return new ArrayList<T>();
        }
        return newArrayList(activeObjectsService.find(type));
    }

    /**
     * Deletes specified entity (classifying by type and ID) with associated relations Entities.
     *
     * @param type Type of Entity.
     * @param id   Id of Entity.
     * @return Indicates that Entity was deleted.
     */
    public boolean delete(@Nonnull final Class<? extends RawEntity> type, @Nonnull final String id) {
        if (type == null || id == null || id.isEmpty()) {
            return false;
        }
        RawEntity[] result = activeObjectsService.find(type, Query.select().where("ID = " + id));

        if (result == null || result.length == 0) {
            return false;
        }
        deleteWithRelations(result);
        return true;
    }

    private void deleteWithRelations(@Nonnull final RawEntity[] entities) {
        for (RawEntity each : entities) {
            RawEntity[] relations = getRelations(each);
            activeObjectsService.delete(relations);
        }
        activeObjectsService.delete(entities);
    }

    private RawEntity[] getRelations(RawEntity entity) {
        if (entity instanceof Plan) {
            return getRelations((Plan) entity);
        }
        if (entity instanceof Domain) {
            return getRelations((Domain) entity);
        }
        if (entity instanceof Component) {
            return getRelations((Component) entity);
        }
        return new RawEntity[]{};
    }

    private RawEntity[] getRelations(@Nonnull final Plan entity) {
        List<RawEntity> result = new ArrayList<RawEntity>();

        result.addAll(Arrays.asList(activeObjectsService.find(PlanToDomainRelation.class, Query.select().where(PlanToDomainRelation.PLAN + "_ID = ?", entity.getID()))));
        result.addAll(Arrays.asList(activeObjectsService.find(PlanToComponentRelation.class, Query.select().where(PlanToComponentRelation.PLAN + "_ID = ?", entity.getID()))));

        return result.toArray(new RawEntity[result.size()]);
    }

    private RawEntity[] getRelations(@Nonnull final Domain entity) {
        return activeObjectsService.find(PlanToDomainRelation.class, Query.select().where(PlanToDomainRelation.DOMAIN + "_ID = ?", entity.getID()));
    }

    private RawEntity[] getRelations(@Nonnull final Component entity) {
        return activeObjectsService.find(PlanToComponentRelation.class, Query.select().where(PlanToComponentRelation.COMPONENT + "_ID = ?", entity.getID()));
    }

    /**
     * @return Event Plans names (map values) sorted by Event Domains names (map keys).
     */
    public Map<String, List<String>> getEventPlansSortedByDomain() {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        for (Domain eachDomain : get(Domain.class)) {
            List<String> plansNames = new ArrayList<String>();
            for (Plan eachPlan : eachDomain.getPlans()) {
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
        deleteAll(PlanToComponentRelation.class);

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
