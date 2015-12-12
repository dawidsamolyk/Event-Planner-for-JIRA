package edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import edu.uz.jira.event.planner.project.plan.model.Domain;
import edu.uz.jira.event.planner.project.plan.model.Plan;
import edu.uz.jira.event.planner.project.plan.model.PlanToTaskRelation;
import edu.uz.jira.event.planner.project.plan.model.Task;
import net.java.ao.Query;

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
        postToLabel.setEventTask(task);
        postToLabel.setEventPlan(plan);
        postToLabel.save();
        return postToLabel;
    }

    private void associatePlanWithDomain(@Nonnull final Plan plan, @Nonnull final String domainName) {
        Domain[] domains = null;

        if (domainName != null) {
            domains = activeObjectsService.find(Domain.class, Query.select().where("NAME = ?", domainName));
        }
        if (domains != null && domains.length == 1 && plan != null && domains[0] != null) {
            plan.setDomain(domains[0]);
        }
    }

    /**
     * @param resource Configuration of Event Organization Plan.
     * @return Newly created and added to database Event Organization Plan.
     */
    public Plan addPlan(@Nonnull final EventPlansConfigResource.ResourceConfiguration resource) {
        Plan result = activeObjectsService.create(Plan.class);

        result.setName(resource.getName());
        result.setDescription(resource.getDescription());
        result.setEstimatedTimeToComplete(resource.getTime());
        associatePlanWithDomain(result, resource.getDomain());

        result.save();
        return result;
    }

    /**
     * @param resource Configuration of Event Organization Domain.
     * @return Newly created and added to database Event Organization Domain.
     */
    public Domain addDomain(@Nonnull final EventPlansConfigResource.ResourceConfiguration resource) {
        Domain result = activeObjectsService.create(Domain.class);

        result.setName(resource.getName());
        result.setDescription(resource.getDescription());

        result.save();
        return result;
    }

    /**
     * @return All Event Organization Plans.
     */
    public List<Plan> getPlans() {
        return newArrayList(activeObjectsService.find(Plan.class));
    }

    /**
     * @return All Event Organization Tasks.
     */
    public List<Task> getAllTasks() {
        return newArrayList(activeObjectsService.find(Task.class));
    }

    private ArrayList<Domain> getDomains() {
        return newArrayList(activeObjectsService.find(Domain.class));
    }

    public Map<String, List<String>> getEventPlansSortedByDomain() {
        Map<String, List<String>> result = new HashMap<String, List<String>>();

        for (Domain eachDomain : getDomains()) {
            List<Plan> plans = Arrays.asList(eachDomain.getEventOrganizationPlans());
            List<String> plansNames = new ArrayList<String>(plans.size());

            for (Plan eachPlan : plans) {
                plansNames.add(eachPlan.getName());
            }

            result.put(eachDomain.getName(), plansNames);
        }

        return result;
    }

}
