package ut.edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.project.plan.EventPlanService;
import edu.uz.jira.event.planner.project.plan.model.Domain;
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
    private ActiveObjects ao;
    private EventPlanService service;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        ao.flushAll();
        service = new EventPlanService(ao);
    }

    @Test
    public void shouldAddDomainFromFullfilledEventDomainConfig() {
        ao.migrate(Domain.class);
        EventDomainRestManager.EventDomainConfig config = new EventDomainRestManager.EventDomainConfig();
        config.setName("Test name");
        config.setDescription("Test description");

        service.addFrom(config);

        assertEquals(1, ao.find(Domain.class).length);
    }

    @Test
    public void shouldNotAddDomainWithNullEventDomainConfig() {
        ao.migrate(Domain.class);

        service.addFrom((EventDomainRestManager.EventDomainConfig) null);

        assertEquals(0, ao.find(Domain.class).length);
    }

    @Test
    public void emptyDomainConfigShouldNotBeAdded() {
        ao.migrate(Domain.class);

        service.addFrom(EventDomainRestManager.EventDomainConfig.createEmpty());

        assertEquals(0, ao.find(Domain.class).length);
    }

    @Test
    public void shouldAddPlanFromFullfilledEventDomainConfig() {
        ao.migrate(Domain.class);
        EventPlanRestManager.EventPlanConfig config = new EventPlanRestManager.EventPlanConfig();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTime("Test time");
        config.setDomains("Test domains");

        service.addFrom(config);

        assertEquals(1, ao.find(Domain.class).length);
    }

    @Test
    public void shouldNotAddPlanWithNullEventDomainConfig() {
        ao.migrate(Domain.class);

        service.addFrom((EventPlanRestManager.EventPlanConfig) null);

        assertEquals(0, ao.find(Domain.class).length);
    }

    @Test
    public void notFullfilledPlanConfigShouldNotBeAdded() {
        ao.migrate(Domain.class);

        service.addFrom(EventPlanRestManager.EventPlanConfig.createEmpty());

        assertEquals(0, ao.find(Domain.class).length);
    }
}
