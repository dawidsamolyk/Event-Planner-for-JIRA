package edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.project.plan.model.*;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToDomainRelation;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Manages database Entities (Active Objects) relations.
 */
class RelationsManager {
    private final ActiveObjects activeObjectsService;
    private final ActiveObjectsHelper helper;

    /**
     * Constructor.
     *
     * @param activeObjectsService Injected {@code ActiveObjects} implementation.
     */
    RelationsManager(@Nonnull final ActiveObjects activeObjectsService) {
        this.activeObjectsService = activeObjectsService;
        helper = new ActiveObjectsHelper(activeObjectsService);
    }

    PlanToDomainRelation associate(@Nonnull final Plan plan, @Nonnull final Domain domain) {
        if (plan == null || domain == null) {
            return null;
        }
        PlanToDomainRelation result = activeObjectsService.create(PlanToDomainRelation.class);
        result.setDomain(domain);
        result.setPlan(plan);
        result.save();
        return result;
    }

    private PlanToComponentRelation associate(@Nonnull final Plan plan, @Nonnull final Component component) {
        if (plan == null || component == null) {
            return null;
        }
        PlanToComponentRelation result = activeObjectsService.create(PlanToComponentRelation.class);
        result.setPlan(plan);
        result.setComponent(component);
        result.save();
        return result;
    }

    private Task associate(@Nonnull final Component component, @Nonnull final Task task) {
        if (component == null || task == null) {
            return null;
        }
        task.setComponent(component);
        task.save();
        return task;
    }

    private SubTask associate(@Nonnull final Task task, @Nonnull final SubTask subTask) {
        if (task == null || subTask == null) {
            return null;
        }
        subTask.setParentTask(task);
        subTask.save();
        return subTask;
    }

    Collection<PlanToComponentRelation> associatePlanWithComponents(@Nonnull final Plan plan, @Nonnull final String[] componentsNames) {
        Collection<PlanToComponentRelation> result = new ArrayList<PlanToComponentRelation>();
        List<Component> components = helper.get(Component.class, Component.NAME + " = ?", componentsNames);
        if (components != null && components.size() > 0 && plan != null) {
            for(Component each : components) {
                result.add(associate(plan, each));
            }
        }
        return result;
    }

    Collection<PlanToDomainRelation> associatePlanWithDomains(@Nonnull final Plan plan, @Nonnull final String[] domainsNames) {
        Collection<PlanToDomainRelation> result = new ArrayList<PlanToDomainRelation>();

        List<Domain> domains = helper.get(Domain.class, Domain.NAME + " = ?", domainsNames);
        if (domains != null && domains.size() > 0 && plan != null) {
            for(Domain each : domains) {
                result.add(associate(plan, each));
            }
        }
        return result;
    }

    boolean associate(@Nonnull final Component component, @Nonnull final String[] tasksNames) {
        if (tasksNames == null || tasksNames.length == 0 || component == null) {
            return false;
        }
        List<Task> tasks = helper.get(Task.class, Task.NAME + " = ?", tasksNames);
        if (tasks == null || tasks.size() == 0) {
            return false;
        }
        for(Task each : tasks) {
            associate(component, each);
        }
        return true;
    }

    boolean associate(@Nonnull final Task task, @Nonnull final String[] subTasksNames) {
        if (subTasksNames == null || subTasksNames.length == 0) {
            // Because Task does not have to contains SubTasks
            return true;
        }
        List<SubTask> subTasks = helper.get(SubTask.class, SubTask.NAME + " = ?", subTasksNames);
        if (subTasks == null || subTasks.size() == 0 || task == null) {
            return false;
        }
        for(SubTask each : subTasks) {
            associate(task, each);
        }
        return true;
    }
}
