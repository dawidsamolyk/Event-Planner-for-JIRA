package edu.uz.jira.event.planner.database.active.objects;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.TaskToComponentRelation;
import edu.uz.jira.event.planner.database.importer.xml.model.AllEventPlans;
import edu.uz.jira.event.planner.database.importer.xml.model.EventPlan;
import edu.uz.jira.event.planner.exception.ActiveObjectSavingException;
import edu.uz.jira.event.planner.project.plan.rest.ActiveObjectWrapper;
import net.java.ao.Entity;
import net.java.ao.Query;
import net.java.ao.RawEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final ActiveObjectsConverter converter;
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
        converter = new ActiveObjectsConverter(activeObjectsService, relationsManager);
    }

    public Entity addFrom(@Nonnull final ActiveObjectWrapper resource) throws ActiveObjectSavingException {
        if (resource == null || !resource.isFullfilled()) {
            throw new ActiveObjectSavingException();
        }
        if (resource instanceof EventPlan) {
            return converter.addFrom((EventPlan) resource);
        }
        if (resource instanceof edu.uz.jira.event.planner.database.importer.xml.model.Domain) {
            return converter.addFrom((edu.uz.jira.event.planner.database.importer.xml.model.Domain) resource);
        }
        if (resource instanceof edu.uz.jira.event.planner.database.importer.xml.model.Component) {
            return converter.addFrom((edu.uz.jira.event.planner.database.importer.xml.model.Component) resource);
        }
        if (resource instanceof edu.uz.jira.event.planner.database.importer.xml.model.Task) {
            return converter.addFrom((edu.uz.jira.event.planner.database.importer.xml.model.Task) resource);
        }
        if (resource instanceof edu.uz.jira.event.planner.database.importer.xml.model.SubTask) {
            return converter.addFrom((edu.uz.jira.event.planner.database.importer.xml.model.SubTask) resource);
        }
        if (resource instanceof AllEventPlans) {
            return converter.addFrom((AllEventPlans) resource);
        }
        throw new ActiveObjectSavingException();
    }

    /**
     * @param type  Type of entities to get.
     * @param query SQL Query which selects and filters data.
     * @return Database objects of specified type.
     */
    public <T extends RawEntity<Integer>> List<T> get(@Nonnull final Class<T> type, @Nonnull final Query query) {
        if (type == null) {
            return new ArrayList<T>();
        }
        return newArrayList(activeObjectsService.find(type, query));
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
        relationsManager.deleteWithRelations(result);
        return true;
    }

    /**
     * @return Event Plans names (map values) sorted by Event Domains names (map keys).
     */
    public Map<String, List<String>> getEventPlansSortedByDomain() {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        for (Domain eachDomain : get(Domain.class, Query.select())) {
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
        deleteAll(SubTaskToTaskRelation.class);
        deleteAll(TaskToComponentRelation.class);

        deleteAll(SubTask.class);
        deleteAll(Task.class);
        deleteAll(Component.class);
        deleteAll(Domain.class);
        deleteAll(Plan.class);
    }

    /**
     * @param type Type of entities to delete.
     */
    private void deleteAll(@Nonnull final Class<? extends RawEntity> type) {
        activeObjectsService.delete(activeObjectsService.find(type));
    }
}
