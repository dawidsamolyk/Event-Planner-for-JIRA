package edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static edu.uz.jira.event.planner.project.plan.EventOrganizationModel.*;

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

    /**
     * Associates Event Organization Task with Event Organiation Plan.
     *
     * @param task Event Organization Task.
     * @param plan Event Organiation Plan.
     * @return Relation object.
     */
    public PlanToTaskRelation associateEventTaskToPlan(@Nonnull final Task task, @Nonnull final Plan plan) {
        PlanToTaskRelation postToLabel = activeObjectsService.create(PlanToTaskRelation.class);
        postToLabel.setEventTask(task);
        postToLabel.setEventPlan(plan);
        postToLabel.save();
        return postToLabel;
    }

    /**
     * @param name Name of the new Event Organization Plan.
     * @return Newly created and added to database Event Organization Plan.
     */
    public Plan add(@Nonnull final String name) {
        Plan result = activeObjectsService.create(Plan.class);
        result.setName(name);
        result.save();
        return result;
    }

    /**
     * @return All Event Organization Plans.
     */
    public List<Plan> getAllPlans() {
        return newArrayList(activeObjectsService.find(Plan.class));
    }

    /**
     * @return All Event Organization Tasks.
     */
    public List<Task> getAllTasks() {
        return newArrayList(activeObjectsService.find(Task.class));
    }

    public Map<String, List<String>> getEventPlans() {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        List<String> list = new ArrayList<String>();
        list.add("Sub-item1");
        list.add("Sub-item2");
        list.add("Sub-item3");
        result.put("Test", list);
        List<String> list2 = new ArrayList<String>();
        list.add("Sub-item8");
        list.add("Sub-item9");
        list.add("Sub-item10");
        result.put("Test2", list2);

        return result;
    }
}
