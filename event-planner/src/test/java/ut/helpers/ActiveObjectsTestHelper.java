package ut.helpers;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.TaskToComponentRelation;

import java.sql.SQLException;

public class ActiveObjectsTestHelper {
    private final ActiveObjects activeObjects;

    public ActiveObjectsTestHelper(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    public Domain createDomain(String name, String description) {
        Domain domain = activeObjects.create(Domain.class);
        domain.setName(name);
        domain.setDescription(description);
        domain.save();
        return domain;
    }

    public SubTask createSubTaskNamed(String name) {
        SubTask result = activeObjects.create(SubTask.class);
        result.setName(name);
        result.save();
        return result;
    }

    public Component createComponentNamed(String name) {
        return createComponent(name, "");
    }

    public Domain createDomainNamed(String name) {
        return createDomain(name, "");
    }

    public Task createTaskNamed(String name) {
        return createTask(name, 0, 10);
    }

    public Component createComponent(String name, String description) {
        Component result = activeObjects.create(Component.class);
        result.setName(name);
        result.setDescription(description);
        result.save();
        return result;
    }

    public Task createTask(String name, int months, int days) {
        Task result = activeObjects.create(Task.class);
        result.setName(name);
        result.setNeededMonthsToComplete(months);
        result.setNeededDaysToComplete(days);
        result.save();
        return result;
    }

    public void createPlanWithDomainAndComponent(String planName, String planDescription, int months, int days, String domainName, String componentName) throws SQLException {
        Domain domain = createDomainNamed(domainName);

        Component component = createComponentNamed(componentName);

        Plan plan = createPlan(planName, planDescription, months, days);

        PlanToDomainRelation relation = activeObjects.create(PlanToDomainRelation.class);
        relation.setPlan(plan);
        relation.setDomain(domain);
        relation.save();

        PlanToComponentRelation relation2 = activeObjects.create(PlanToComponentRelation.class);
        relation2.setComponent(component);
        relation2.setPlan(plan);
        relation2.save();
    }

    public PlanToDomainRelation associate(Plan plan, Domain domain) {
        PlanToDomainRelation relation = activeObjects.create(PlanToDomainRelation.class);
        relation.setPlan(plan);
        relation.setDomain(domain);
        relation.save();
        return relation;
    }

    public PlanToComponentRelation associate(Plan plan, Component component) {
        PlanToComponentRelation relation = activeObjects.create(PlanToComponentRelation.class);
        relation.setPlan(plan);
        relation.setComponent(component);
        relation.save();
        return relation;
    }

    public Plan createPlan(String planName, String planDescription, int months, int days) {
        Plan plan = activeObjects.create(Plan.class);
        plan.setName(planName);
        plan.setDescription(planDescription);
        plan.setNeededMonthsToComplete(months);
        plan.setNeededDaysToComplete(days);
        plan.save();
        return plan;
    }

    public Plan createPlanNamed(String name) {
        return createPlan(name, "", 1, 0);
    }

    public SubTaskToTaskRelation associate(Task thirdTask, SubTask thirdSubTask) {
        SubTaskToTaskRelation result = activeObjects.create(SubTaskToTaskRelation.class);
        result.setSubTask(thirdSubTask);
        result.setTask(thirdTask);
        result.save();
        return result;
    }

    public TaskToComponentRelation associate(Component secondComponent, Task fourthTask) {
        TaskToComponentRelation result = activeObjects.create(TaskToComponentRelation.class);
        result.setComponent(secondComponent);
        result.setTask(fourthTask);
        result.save();
        return result;
    }
}
