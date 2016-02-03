package edu.uz.jira.event.planner.database.active.objects;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToCategoryRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.xml.model.*;
import edu.uz.jira.event.planner.exception.ActiveObjectSavingException;
import edu.uz.jira.event.planner.project.plan.rest.ActiveObjectWrapper;
import net.java.ao.Entity;
import net.java.ao.Query;
import net.java.ao.RawEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Converts XML representation of Event Plan Template elements into Active Objects (database entities).
 */
class ActiveObjectsConverter {
    private final ActiveObjects activeObjectsService;
    private final RelationsManager relationsManager;

    ActiveObjectsConverter(@Nonnull final ActiveObjects activeObjectsService,
                           @Nonnull final RelationsManager relationsManager) {
        this.activeObjectsService = activeObjectsService;
        this.relationsManager = relationsManager;
    }

    private RawEntity getFrom(ActiveObjectWrapper wrapper) {
        return activeObjectsService.find(wrapper.getWrappedType(), Query.select().where("NAME = ?", wrapper.getName()))[0];
    }

    Plan addFrom(@Nonnull final PlanTemplate resource) throws ActiveObjectSavingException {
        Plan result = activeObjectsService.create(Plan.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());

        List<Category> categories = new ArrayList<Category>();
        for (EventCategory each : resource.getEventCategory()) {
            if (each.isFullfilled() && notExists(each)) {
                categories.add(addFrom(each));
            } else {
                categories.add((Category) getFrom(each));
            }
        }

        Collection<PlanToCategoryRelation> planToCategoryRelations = relationsManager.associatePlanWithCategories(result, categories);
        if (planToCategoryRelations.isEmpty()) {
            relationsManager.deleteWithRelations(result);
            throw new ActiveObjectSavingException();
        }

        List<Component> components = new ArrayList<Component>();
        for (ComponentTemplate each : resource.getComponent()) {
            if (each.isFullfilled()) {
                components.add(addFrom(each));
            }
        }

        Collection<PlanToComponentRelation> planToComponentRelations = relationsManager.associatePlanWithComponents(result, components);
        if (planToComponentRelations.isEmpty()) {
            relationsManager.deleteWithRelations(result);
            throw new ActiveObjectSavingException();
        }

        result.save();
        return result;
    }

    private boolean notExists(@Nonnull final EventCategory category) {
        return activeObjectsService.find(category.getWrappedType(), Query.select().where("NAME = ?", category.getName())).length == 0;
    }

    Category addFrom(@Nonnull final EventCategory resource) {
        Category result = activeObjectsService.create(Category.class);
        result.setName(resource.getName());
        result.save();
        return result;
    }

    Component addFrom(@Nonnull final ComponentTemplate resource) throws ActiveObjectSavingException {
        Component result = activeObjectsService.create(Component.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());

        List<Task> tasks = new ArrayList<Task>();
        for (TaskTemplate each : resource.getTask()) {
            if (each.isFullfilled()) {
                tasks.add(addFrom(each));
            }
        }

        boolean valid = relationsManager.associate(result, tasks);
        if (!valid) {
            relationsManager.deleteWithRelations(result);
            throw new ActiveObjectSavingException();
        }

        result.save();
        return result;
    }

    Task addFrom(@Nonnull final TaskTemplate resource) throws ActiveObjectSavingException {
        Task result = activeObjectsService.create(Task.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.setNeededDaysToComplete(resource.getNeededDaysBeforeEvent());
        result.setNeededMonthsToComplete(resource.getNeededMonthsBeforeEvent());

        List<SubTask> subTasks = new ArrayList<SubTask>();
        for (SubTaskTemplate each : resource.getSubTask()) {
            if (each.isFullfilled()) {
                subTasks.add(addFrom(each));
            }
        }

        boolean valid = relationsManager.associate(result, subTasks);
        if (!valid) {
            relationsManager.deleteWithRelations(result);
            throw new ActiveObjectSavingException();
        }

        result.save();
        return result;
    }

    SubTask addFrom(@Nonnull final SubTaskTemplate resource) {
        SubTask result = activeObjectsService.create(SubTask.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.save();
        return result;
    }

    Entity addFrom(@Nonnull final EventPlanTemplates resource) throws ActiveObjectSavingException {
        for (PlanTemplate eachPlan : resource.getEventPlanTemplate()) {
            if (eachPlan.isFullfilled()) {
                addFrom(eachPlan);
            }
        }
        return null;
    }
}
