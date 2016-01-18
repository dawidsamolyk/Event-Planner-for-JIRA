package edu.uz.jira.event.planner.database.active.objects;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.SubTask;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToCategoryRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.xml.model.*;
import edu.uz.jira.event.planner.exception.ActiveObjectSavingException;
import net.java.ao.Entity;

import javax.annotation.Nonnull;
import java.util.Collection;

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

    Plan addFrom(@Nonnull final PlanTemplate resource) throws ActiveObjectSavingException {
        Plan result = activeObjectsService.create(Plan.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());

        Collection<PlanToCategoryRelation> planToCategoryRelations = relationsManager.associatePlanWithDomains(result, resource.getCategoriesNames());
        if (planToCategoryRelations.isEmpty()) {
            relationsManager.deleteWithRelations(result);
            throw new ActiveObjectSavingException();
        }

        Collection<PlanToComponentRelation> planToComponentRelations = relationsManager.associatePlanWithComponents(result, resource.getComponentsNames());
        if (planToComponentRelations.isEmpty()) {
            relationsManager.deleteWithRelations(result);
            throw new ActiveObjectSavingException();
        }

        result.save();
        return result;
    }

    Category addFrom(@Nonnull final EventCategory resource) {
        Category result = activeObjectsService.create(Category.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.save();
        return result;
    }

    Component addFrom(@Nonnull final ComponentTemplate resource) throws ActiveObjectSavingException {
        Component result = activeObjectsService.create(Component.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());

        boolean valid = relationsManager.associate(result, resource.getTasksNames());
        if (!valid) {
            relationsManager.deleteWithRelations(result);
            throw new ActiveObjectSavingException();
        }

        result.save();
        return result;
    }

    Task addFrom(TaskTemplate resource) throws ActiveObjectSavingException {
        Task result = activeObjectsService.create(Task.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.setNeededDaysToComplete(resource.getNeededDaysBeforeEvent());
        result.setNeededMonthsToComplete(resource.getNeededMonthsBeforeEvent());

        boolean valid = relationsManager.associate(result, resource.getSubTasksNames());
        if (!valid) {
            relationsManager.deleteWithRelations(result);
            throw new ActiveObjectSavingException();
        }

        result.save();
        return result;
    }

    SubTask addFrom(SubTaskTemplate resource) {
        SubTask result = activeObjectsService.create(SubTask.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.save();
        return result;
    }

    Entity addFrom(final EventPlanTemplates resource) throws ActiveObjectSavingException {
        for (PlanTemplate eachPlan : resource.getEventPlanTemplate()) {
            for (EventCategory eachEventCategory : eachPlan.getEventCategory()) {
                addFrom(eachEventCategory);
            }

            for (ComponentTemplate eachComponentTemplate : eachPlan.getComponent()) {
                for (TaskTemplate eachTaskTemplate : eachComponentTemplate.getTask()) {
                    for (SubTaskTemplate eachSubTaskTemplate : eachTaskTemplate.getSubTask()) {
                        addFrom(eachSubTaskTemplate);
                    }
                    addFrom(eachTaskTemplate);
                }
                addFrom(eachComponentTemplate);
            }

            addFrom(eachPlan);
        }
        return null;
    }
}
