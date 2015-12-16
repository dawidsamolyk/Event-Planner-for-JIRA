package ut.edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import edu.uz.jira.event.planner.project.plan.EventPlanService;
import edu.uz.jira.event.planner.project.plan.model.*;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventDomainRestManager;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventPlanRestManager;
import net.java.ao.EntityManager;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.Hsql;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import ut.helpers.TestActiveObjects;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
@RunWith(ActiveObjectsJUnitRunner.class)
@Jdbc(Hsql.class)
@NameConverters
public class EventPlanServiceTest {
    private EntityManager entityManager;
    private ActiveObjects activeObjects;
    private EventPlanService service;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.migrate(Domain.class, Plan.class, Component.class, Plan.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToDomainRelation.class);
        service = new EventPlanService(activeObjects);
    }

    @Test
    public void shouldAddDomainFromFullfilledEventDomainConfig() {
        EventDomainRestManager.Configuration configuration = new EventDomainRestManager.Configuration();
        configuration.setName("Test name");
        configuration.setDescription("Test description");

        service.addFrom(configuration);

        assertEquals(1, activeObjects.find(Domain.class).length);
    }

    @Test
    public void shouldNotAddDomainWithNullEventDomainConfig() {
        service.addFrom((EventDomainRestManager.Configuration) null);

        assertEquals(0, activeObjects.find(Domain.class).length);
    }

    @Test
    public void emptyDomainConfigShouldNotBeAdded() {
        service.addFrom(EventDomainRestManager.Configuration.createEmpty());

        assertEquals(0, activeObjects.find(Domain.class).length);
    }

    @Test
    public void shouldAddPlanFromFullfilledEventDomainConfig() {
        EventPlanRestManager.Configuration config = new EventPlanRestManager.Configuration();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTime("Test time");
        config.setDomains(new String[]{"Test domain"});
        config.setComponents(new String[]{"Test component"});

        service.addFrom(config);

        assertEquals(1, activeObjects.find(Plan.class).length);
    }

    @Test
    public void shouldNotAddPlanWithNullEventDomainConfig() {
        service.addFrom((EventPlanRestManager.Configuration) null);

        assertEquals(0, activeObjects.find(Plan.class).length);
    }

    @Test
    public void notFullfilledPlanConfigShouldNotBeAdded() {
        service.addFrom(EventPlanRestManager.Configuration.createEmpty());

        assertEquals(0, activeObjects.find(Plan.class).length);
    }
}
