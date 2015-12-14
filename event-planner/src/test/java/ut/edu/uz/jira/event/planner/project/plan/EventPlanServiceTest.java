package ut.edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.project.plan.EventPlanService;
import edu.uz.jira.event.planner.project.plan.model.Domain;
import edu.uz.jira.event.planner.project.plan.model.Plan;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventDomainRestManager;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventPlanRestManager;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ut.helpers.TestActiveObjects;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(ActiveObjectsJUnitRunner.class)
public class EventPlanServiceTest {
    private EntityManager entityManager;
    private ActiveObjects activeObjects;
    private EventPlanService service;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.flushAll();
        activeObjects.migrate(Domain.class, Plan.class);
        service = new EventPlanService(activeObjects);
    }

    @Test
    public void shouldAddDomainFromFullfilledEventDomainConfig() {
        EventDomainRestManager.EventDomainConfig config = new EventDomainRestManager.EventDomainConfig();
        config.setName("Test name");
        config.setDescription("Test description");

        service.addFrom(config);

        assertEquals(1, activeObjects.find(Domain.class).length);
    }

    @Test
    public void shouldNotAddDomainWithNullEventDomainConfig() {
        service.addFrom((EventDomainRestManager.EventDomainConfig) null);

        assertEquals(0, activeObjects.find(Domain.class).length);
    }

    @Test
    public void emptyDomainConfigShouldNotBeAdded() {
        service.addFrom(EventDomainRestManager.EventDomainConfig.createEmpty());

        assertEquals(0, activeObjects.find(Domain.class).length);
    }

    @Test
    public void shouldAddPlanFromFullfilledEventDomainConfig() {
        EventPlanRestManager.EventPlanConfig config = new EventPlanRestManager.EventPlanConfig();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTime("Test time");
        config.setDomains("Test domains");

        service.addFrom(config);

        assertEquals(1, activeObjects.find(Plan.class).length);
    }

    @Test
    public void shouldNotAddPlanWithNullEventDomainConfig() {
        service.addFrom((EventPlanRestManager.EventPlanConfig) null);

        assertEquals(0, activeObjects.find(Plan.class).length);
    }

    @Test
    public void notFullfilledPlanConfigShouldNotBeAdded() {
        service.addFrom(EventPlanRestManager.EventPlanConfig.createEmpty());

        assertEquals(0, activeObjects.find(Plan.class).length);
    }
}
