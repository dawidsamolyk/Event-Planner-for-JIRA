package ut.edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import edu.uz.jira.event.planner.project.plan.EventOrganizationPlanService;
import edu.uz.jira.event.planner.project.plan.model.Plan;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
public class EventOrganizationPlanServiceTest {
    private EntityManager entityManager;
    private ActiveObjects activeObjects;
    private EventOrganizationPlanService eventOrganizationPlanService;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        activeObjects = new TestActiveObjects(entityManager);
        eventOrganizationPlanService = new EventOrganizationPlanService(activeObjects);
    }

    @Test
    public void testAdd() throws Exception {
        activeObjects.migrate(Plan.class);
        activeObjects.flushAll();

        eventOrganizationPlanService.add("Test");

        Plan[] todos = activeObjects.find(Plan.class);
        assertEquals(1, todos.length);
    }
}
