package ut.edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.test.TestActiveObjects;
import edu.uz.jira.event.planner.project.plan.EventOrganizationPlanService;
import edu.uz.jira.event.planner.project.plan.model.Domain;
import edu.uz.jira.event.planner.project.plan.model.Plan;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

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
        activeObjects.migrate(Plan.class);
        activeObjects.flushAll();
        eventOrganizationPlanService = new EventOrganizationPlanService(activeObjects);
    }

    @Test
    public void shouldAddPlanWithSpecifiedName() throws Exception {
        eventOrganizationPlanService.addPlanNamed("Test");

        Plan[] allData = activeObjects.find(Plan.class);
        assertEquals(1, allData.length);
    }

    @Test
    public void shouldGiveAllEventPlansNamesSortedByDomains() {
        Domain domain = addDomainNamed("Domain name");

        Map<String, List<String>> result = eventOrganizationPlanService.getEventPlans();

        assertTrue(result.keySet().contains("Domain name"));
    }

    private Domain addDomainNamed(String name) {
        Domain domain = activeObjects.create(Domain.class);
        domain.setName(name);
        domain.save();
        return domain;
    }
}
