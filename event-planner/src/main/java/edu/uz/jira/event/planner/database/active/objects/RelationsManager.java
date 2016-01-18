package edu.uz.jira.event.planner.database.active.objects;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToCategoryRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.TaskToComponentRelation;
import net.java.ao.Query;
import net.java.ao.RawEntity;

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

    public void deleteWithRelations(@Nonnull final RawEntity... entities) {
        for (RawEntity each : entities) {
            RawEntity[] relations = getRelations(each);
            activeObjectsService.delete(relations);
        }
        activeObjectsService.delete(entities);
    }

    private PlanToCategoryRelation associate(@Nonnull final Plan plan, @Nonnull final Category category) {
        PlanToCategoryRelation result = activeObjectsService.create(PlanToCategoryRelation.class);
        result.setCategory(category);
        result.setPlan(plan);
        result.save();
        return result;
    }

    private PlanToComponentRelation associate(@Nonnull final Plan plan, @Nonnull final Component component) {
        PlanToComponentRelation result = activeObjectsService.create(PlanToComponentRelation.class);
        result.setPlan(plan);
        result.setComponent(component);
        result.save();
        return result;
    }

    private TaskToComponentRelation associate(@Nonnull final Component component, @Nonnull final Task task) {
        TaskToComponentRelation result = activeObjectsService.create(TaskToComponentRelation.class);
        result.setComponent(component);
        result.setTask(task);
        result.save();
        return result;
    }

    private SubTaskToTaskRelation associate(@Nonnull final Task task, @Nonnull final SubTask subTask) {
        SubTaskToTaskRelation result = activeObjectsService.create(SubTaskToTaskRelation.class);
        result.setSubTask(subTask);
        result.setTask(task);
        result.save();
        return result;
    }

    Collection<PlanToComponentRelation> associatePlanWithComponents(@Nonnull final Plan plan, @Nonnull final String[] componentsNames) {
        Collection<PlanToComponentRelation> result = new ArrayList<PlanToComponentRelation>();
        List<Component> components = helper.get(Component.class, Component.NAME + " = ?", componentsNames);
        if (components != null && components.size() > 0 && plan != null) {
            for (Component each : components) {
                result.add(associate(plan, each));
            }
        }
        helper.updateNeededDaysToComplete(plan);
        return result;
    }



    Collection<PlanToCategoryRelation> associatePlanWithDomains(@Nonnull final Plan plan, @Nonnull final String[] domainsNames) {
        Collection<PlanToCategoryRelation> result = new ArrayList<PlanToCategoryRelation>();

        List<Category> categories = helper.get(Category.class, Category.NAME + " = ?", domainsNames);
        if (categories != null && categories.size() > 0 && plan != null) {
            for (Category each : categories) {
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
        for (Task each : tasks) {
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
        for (SubTask each : subTasks) {
            associate(task, each);
        }
        return true;
    }

    private RawEntity[] getRelations(RawEntity entity) {
        if (entity instanceof Plan) {
            return getRelations((Plan) entity);
        }
        if (entity instanceof Category) {
            return getRelations((Category) entity);
        }
        if (entity instanceof Component) {
            return getRelations((Component) entity);
        }
        return new RawEntity[]{};
    }

    private RawEntity[] getRelations(@Nonnull final Plan entity) {
        List<RawEntity> result = new ArrayList<RawEntity>();

        result.addAll(Arrays.asList(activeObjectsService.find(PlanToCategoryRelation.class, Query.select().where(PlanToCategoryRelation.PLAN + "_ID = ?", entity.getID()))));
        result.addAll(Arrays.asList(activeObjectsService.find(PlanToComponentRelation.class, Query.select().where(PlanToComponentRelation.PLAN + "_ID = ?", entity.getID()))));

        return result.toArray(new RawEntity[result.size()]);
    }

    private RawEntity[] getRelations(@Nonnull final Category entity) {
        return activeObjectsService.find(PlanToCategoryRelation.class, Query.select().where(PlanToCategoryRelation.CATEGORY + "_ID = ?", entity.getID()));
    }

    private RawEntity[] getRelations(@Nonnull final Component entity) {
        return activeObjectsService.find(PlanToComponentRelation.class, Query.select().where(PlanToComponentRelation.COMPONENT + "_ID = ?", entity.getID()));
    }
}
