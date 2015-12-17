package ut.helpers;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.project.plan.model.*;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToDomainRelation;

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
        return createSubTask(name, "");
    }

    public SubTask createSubTask(String name, String time) {
        SubTask result = activeObjects.create(SubTask.class);
        result.setName(name);
        result.setTimeToComplete(time);
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
        return createTask(name, "");
    }

    public Component createComponent(String name, String description) {
        Component result = activeObjects.create(Component.class);
        result.setName(name);
        result.setDescription(description);
        result.save();
        return result;
    }

    public Task createTask(String name, String time) {
        Task result = activeObjects.create(Task.class);
        result.setName(name);
        result.setTimeToComplete(time);
        result.save();
        return result;
    }

    public void createPlanWithDomainAndComponent(String planName, String planDescription, String planTime, String domainName, String componentName) throws SQLException {
        Domain domain = createDomainNamed(domainName);

        Component component = createComponentNamed(componentName);

        Plan plan = createPlan(planName, planDescription, planTime);

        PlanToDomainRelation relation = activeObjects.create(PlanToDomainRelation.class);
        relation.setPlan(plan);
        relation.setDomain(domain);
        relation.save();

        PlanToComponentRelation relation2 = activeObjects.create(PlanToComponentRelation.class);
        relation2.setComponent(component);
        relation2.setPlan(plan);
        relation2.save();
    }

    public Plan createPlan(String planName, String planDescription, String planTime) {
        Plan plan = activeObjects.create(Plan.class);
        plan.setName(planName);
        plan.setDescription(planDescription);
        plan.setTimeToComplete(planTime);
        plan.save();
        return plan;
    }
}
