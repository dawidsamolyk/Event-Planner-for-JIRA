package ut.edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.project.plan.EventOrganizationPlanService;
import edu.uz.jira.event.planner.project.plan.model.Domain;
import edu.uz.jira.event.planner.project.plan.rest.EventDomainRestManager;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(ActiveObjectsJUnitRunner.class)
public class EventOrganizationPlanServiceTest {
    private EntityManager entityManager;
    private ActiveObjects ao;
    private EventOrganizationPlanService service;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        ao = new TestActiveObjects(entityManager);
        ao.flushAll();
        service = new EventOrganizationPlanService(ao);
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
}
