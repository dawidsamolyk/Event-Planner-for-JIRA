package ut.helpers;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.project.plan.model.Component;
import edu.uz.jira.event.planner.project.plan.model.Domain;
import edu.uz.jira.event.planner.project.plan.model.SubTask;
import edu.uz.jira.event.planner.project.plan.model.Task;
import ut.edu.uz.jira.event.planner.project.plan.EventPlanServiceTest;

public class ActiveObjectsTestHelper {
    private final ActiveObjects activeObjects;
    public ActiveObjectsTestHelper(ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    public SubTask createSubTaskNamed(String name, EventPlanServiceTest eventPlanServiceTest) {
        SubTask result = activeObjects.create(SubTask.class);
        result.setName(name);
        result.save();
        return result;
    }

    public Component createComponentNamed(String name) {
        Component result = activeObjects.create(Component.class);
        result.setName(name);
        result.save();
        return result;
    }

    public Domain createDomainNamed(String name) {
        Domain result = activeObjects.create(Domain.class);
        result.setName(name);
        result.save();
        return result;
    }

    public Task createTaskNamed(String name) {
        Task result = activeObjects.create(Task.class);
        result.setName(name);
        result.save();
        return result;
    }
}
