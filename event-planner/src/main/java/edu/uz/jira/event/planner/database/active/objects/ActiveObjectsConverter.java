package edu.uz.jira.event.planner.database.active.objects;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.database.importer.xml.model.AllEventPlans;
import edu.uz.jira.event.planner.database.importer.xml.model.EventPlan;
import edu.uz.jira.event.planner.exception.ActiveObjectSavingException;
import net.java.ao.Entity;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Converts XML representation of Event Plan elements into Active Objects (database entities).
 */
class ActiveObjectsConverter {
    private final ActiveObjects activeObjectsService;
    private final RelationsManager relationsManager;

    /**
     * Constructor.
     *
     * @param activeObjectsService Injected {@code ActiveObjects} implementation.
     * @param relationsManager     Relations manager.
     */
    public ActiveObjectsConverter(@Nonnull final ActiveObjects activeObjectsService,
                                  @Nonnull final RelationsManager relationsManager) {
        this.activeObjectsService = activeObjectsService;
        this.relationsManager = relationsManager;
    }

    protected Plan addFrom(@Nonnull final EventPlan resource) throws ActiveObjectSavingException {
        Plan result = activeObjectsService.create(Plan.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.setNeededDaysToComplete(resource.getNeededDays());
        result.setNeededMonthsToComplete(resource.getNeededMonths());

        Collection<PlanToDomainRelation> planToDomainRelations = relationsManager.associatePlanWithDomains(result, resource.getDomainsNames());
        if (planToDomainRelations.isEmpty()) {
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

    protected Domain addFrom(@Nonnull final edu.uz.jira.event.planner.database.importer.xml.model.Domain resource) {
        Domain result = activeObjectsService.create(Domain.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.save();
        return result;
    }

    protected Component addFrom(@Nonnull final edu.uz.jira.event.planner.database.importer.xml.model.Component resource) throws ActiveObjectSavingException {
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

    protected Task addFrom(edu.uz.jira.event.planner.database.importer.xml.model.Task resource) throws ActiveObjectSavingException {
        Task result = activeObjectsService.create(Task.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.setNeededDaysToComplete(resource.getNeededDays());
        result.setNeededMonthsToComplete(resource.getNeededMonths());

        boolean valid = relationsManager.associate(result, resource.getSubTasksNames());
        if (!valid) {
            relationsManager.deleteWithRelations(result);
            throw new ActiveObjectSavingException();
        }

        result.save();
        return result;
    }

    protected SubTask addFrom(edu.uz.jira.event.planner.database.importer.xml.model.SubTask resource) {
        SubTask result = activeObjectsService.create(SubTask.class);
        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.save();
        return result;
    }

    protected Entity addFrom(final AllEventPlans resource) throws ActiveObjectSavingException {
        for (EventPlan eachPlan : resource.getEventPlan()) {
            for (edu.uz.jira.event.planner.database.importer.xml.model.Domain eachDomain : eachPlan.getDomain()) {
                addFrom(eachDomain);
            }

            for (edu.uz.jira.event.planner.database.importer.xml.model.Component eachComponent : eachPlan.getComponent()) {
                for (edu.uz.jira.event.planner.database.importer.xml.model.Task eachTask : eachComponent.getTask()) {
                    for (edu.uz.jira.event.planner.database.importer.xml.model.SubTask eachSubTask : eachTask.getSubTask()) {
                        addFrom(eachSubTask);
                    }
                    addFrom(eachTask);
                }
                addFrom(eachComponent);
            }

            addFrom(eachPlan);
        }
        return null;
    }
}
