package ut.edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import edu.uz.jira.event.planner.project.plan.ActiveObjectsService;
import edu.uz.jira.event.planner.project.plan.model.*;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.project.plan.rest.manager.*;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.Hsql;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import ut.helpers.ActiveObjectsTestHelper;
import ut.helpers.TestActiveObjects;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@Transactional
@RunWith(ActiveObjectsJUnitRunner.class)
@Jdbc(Hsql.class)
@NameConverters
public class ActiveObjectsServiceTest {
    private EntityManager entityManager;
    private ActiveObjects activeObjects;
    private ActiveObjectsTestHelper activeObjectsHelper;
    private ActiveObjectsService service;

    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.migrate(Domain.class, Plan.class, Component.class, Plan.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToDomainRelation.class);
        service = new ActiveObjectsService(activeObjects);
        activeObjects.flushAll();
        service.clearDatabase();
        activeObjectsHelper = new ActiveObjectsTestHelper(activeObjects);
    }

    @Test
    public void shouldNotAddAnyWithNullConfiguration() {
        service.addFrom((EventDomainRestManager.Configuration) null);
        assertEquals(0, activeObjects.count(Domain.class));

        service.addFrom((EventPlanRestManager.Configuration) null);
        assertEquals(0, activeObjects.count(Plan.class));

        service.addFrom((EventComponentRestManager.Configuration) null);
        assertEquals(0, activeObjects.count(Component.class));

        service.addFrom((EventSubTaskRestManager.Configuration) null);
        assertEquals(0, activeObjects.count(SubTask.class));

        service.addFrom((EventTaskRestManager.Configuration) null);
        assertEquals(0, activeObjects.count(Task.class));
    }

    @Test
    public void shouldNotAddAnyWithEmptyConfiguration() {
        service.addFrom(EventDomainRestManager.Configuration.createEmpty());
        assertEquals(0, activeObjects.count(Domain.class));

        service.addFrom(EventPlanRestManager.Configuration.createEmpty());
        assertEquals(0, activeObjects.count(Plan.class));

        service.addFrom(EventComponentRestManager.Configuration.createEmpty());
        assertEquals(0, activeObjects.count(Component.class));

        service.addFrom(EventSubTaskRestManager.Configuration.createEmpty());
        assertEquals(0, activeObjects.count(SubTask.class));

        service.addFrom(EventTaskRestManager.Configuration.createEmpty());
        assertEquals(0, activeObjects.count(Task.class));
    }

    @Test
    public void shouldNotAddAnyWhenConfugirationIsNotFullfilled() {
        EventComponentRestManager.Configuration mockDomainConfig = mock(EventComponentRestManager.Configuration.class);
        Mockito.when(mockDomainConfig.isFullfilled()).thenReturn(false);
        service.addFrom(mockDomainConfig);
        assertEquals(0, activeObjects.count(Domain.class));

        EventPlanRestManager.Configuration mockPlanConfig = mock(EventPlanRestManager.Configuration.class);
        Mockito.when(mockPlanConfig.isFullfilled()).thenReturn(false);
        service.addFrom(mockPlanConfig);
        assertEquals(0, activeObjects.count(Plan.class));

        EventComponentRestManager.Configuration mockComponentConfig = mock(EventComponentRestManager.Configuration.class);
        Mockito.when(mockComponentConfig.isFullfilled()).thenReturn(false);
        service.addFrom(mockComponentConfig);
        assertEquals(0, activeObjects.count(Component.class));

        EventSubTaskRestManager.Configuration mockSubTaskConfig = mock(EventSubTaskRestManager.Configuration.class);
        Mockito.when(mockSubTaskConfig.isFullfilled()).thenReturn(false);
        service.addFrom(mockComponentConfig);
        assertEquals(0, activeObjects.count(SubTask.class));

        EventTaskRestManager.Configuration mockTaskConfig = mock(EventTaskRestManager.Configuration.class);
        Mockito.when(mockTaskConfig.isFullfilled()).thenReturn(false);
        service.addFrom(mockTaskConfig);
        assertEquals(0, activeObjects.count(Task.class));
    }

    @Test
    public void shouldNotGetAnyIfInputTypeIsNull() {
        List<RawEntity<Integer>> result = service.get(null, Query.select());

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnArrayOfDatabaseObjectsOfSpecifiedType() {
        activeObjects.create(Plan.class).save();
        activeObjects.create(Plan.class).save();
        activeObjects.create(Task.class).save();
        activeObjects.create(Domain.class).save();

        List<Plan> result = service.get(Plan.class, Query.select());

        assertEquals(2, result.size());
    }

    @Test
    public void planShouldNotBeAddedIfHasFullfilledConfigurationButNoRelatedObjects() {
        EventPlanRestManager.Configuration config = new EventPlanRestManager.Configuration();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTime("Test time");
        config.setDomains(new String[]{"Test domain"});
        config.setComponents(new String[]{"Test component"});

        service.addFrom(config);

        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void planShouldBeAddedIfHasFullfilledConfigurationAndHasRelatedObjects() {
        Domain domain = activeObjectsHelper.createDomainNamed("Test domain");
        Component component = activeObjectsHelper.createComponentNamed("Test component");
        EventPlanRestManager.Configuration config = new EventPlanRestManager.Configuration();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTime("Test time");
        config.setDomains(new String[]{domain.getName()});
        config.setComponents(new String[]{component.getName()});

        service.addFrom(config);

        assertEquals(1, activeObjects.count(Plan.class));
    }

    @Test
    public void domainShouldBeAddedIfHasFullfilledConfiguration() {
        EventDomainRestManager.Configuration configuration = new EventDomainRestManager.Configuration();
        configuration.setName("Test name");
        configuration.setDescription("Test description");

        service.addFrom(configuration);

        assertEquals(1, activeObjects.count(Domain.class));
    }

    @Test
    public void taskShouldBeAddedIfHasFullfilledConfigurationWithRelatedSubTasks() {
        SubTask firstSubTask = activeObjectsHelper.createSubTaskNamed("Test 1");
        SubTask secondSubTask = activeObjectsHelper.createSubTaskNamed("Test 2");
        EventTaskRestManager.Configuration config = new EventTaskRestManager.Configuration();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTime("Test time");
        config.setSubtasks(new String[]{firstSubTask.getName(), secondSubTask.getName()});

        service.addFrom(config);

        assertEquals(1, activeObjects.find(Task.class).length);
        Task[] createdTask = activeObjects.find(Task.class, Query.select().where(Task.NAME + " = ?", config.getName()));
        assertEquals(2, createdTask[0].getSubTasks().length);
    }

    @Test
    public void componentShouldBeAddedIfHasFullfilledConfiguration() {
        Task task = activeObjectsHelper.createTaskNamed("Test");
        EventComponentRestManager.Configuration config = new EventComponentRestManager.Configuration();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTasks(new String[]{task.getName()});

        service.addFrom(config);

        assertEquals(1, activeObjects.count(Component.class));
    }

    @Test
    public void componentShouldBeAddedIfHasFullfilledConfigurationWithRelatedTasks() {
        Task firstTask = activeObjectsHelper.createTaskNamed("Test 1");
        Task secondTask = activeObjectsHelper.createTaskNamed("Test 2");
        EventComponentRestManager.Configuration config = new EventComponentRestManager.Configuration();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTasks(new String[]{firstTask.getName(), secondTask.getName()});

        service.addFrom(config);

        assertEquals(1, activeObjects.find(Component.class).length);
        Component[] createdTask = activeObjects.find(Component.class, Query.select().where(Component.NAME + " = ?", config.getName()));
        assertEquals(2, createdTask[0].getTasks().length);
    }

    @Test
    public void subTaskShouldBeAddedIfHasFullfilledConfiguration() {
        EventSubTaskRestManager.Configuration config = new EventSubTaskRestManager.Configuration();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTime("Test time");

        service.addFrom(config);

        assertEquals(1, activeObjects.count(SubTask.class));
    }

//    @Test
//    public void planShouldNotBeAddIfCannotRelateItWithDomain() {
//        Plan plan = activeObjectsHelper.createPlan("Name", "Description", "Time");
//        Component component = activeObjectsHelper.createComponentNamed("Test componnet");
//        EventPlanRestManager.Configuration config = new EventPlanRestManager.Configuration();
//        config.setName("Name");
//        config.setDescription("Description");
//        config.setTime("Time");
//        config.setDomains(new String[]{"Nonexistent domain"});
//        config.setComponents(new String[]{component.getName()});
//
//        service.addFrom(config);
//
//        assertEquals(0, activeObjects.count(Plan.class));
//    }
//
//    @Test
//    public void planShouldNotBeAddIfCannotRelateItWithComponent() {
//        Plan plan = activeObjectsHelper.createPlan("Name", "Description", "Time");
//        Domain domain = activeObjectsHelper.createDomainNamed("test domain");
//        EventPlanRestManager.Configuration config = new EventPlanRestManager.Configuration();
//        config.setName("Name");
//        config.setDescription("Description");
//        config.setTime("Time");
//        config.setComponents(new String[]{"Nonexistent component"});
//        config.setDomains(new String[]{domain.getName()});
//
//        service.addFrom(config);
//
//        assertEquals(0, activeObjects.count(Plan.class));
//    }
//
//    @Test
//    public void componentShouldNotBeAddIfCannotRelateItWithTasks() {
//        activeObjectsHelper.createComponent("Name", "Description");
//        EventComponentRestManager.Configuration config = new EventComponentRestManager.Configuration();
//        config.setName("Name");
//        config.setDescription("Description");
//        config.setTasks(new String[]{"Nonexistent task"});
//
//        service.addFrom(config);
//
//        assertEquals(0, activeObjects.count(Component.class));
//    }
//
//    @Test
//    public void taskShouldNotBeAddIfCannotRelateItWithSubTasks() {
//        activeObjectsHelper.createTask("Name", "Description");
//        EventTaskRestManager.Configuration config = new EventTaskRestManager.Configuration();
//        config.setName("Name");
//        config.setDescription("Description");
//        config.setSubtasks(new String[]{"Nonexistent subtask"});
//
//        service.addFrom(config);
//
//        assertEquals(0, activeObjects.count(Task.class));
//    }

    @Test
    public void shouldClearDatabase() {
        Task task = activeObjectsHelper.createTask("test name", "test description");
        SubTask subTask = activeObjectsHelper.createSubTask("name", "");
        subTask.setParentTask(task);
        Domain domain = activeObjectsHelper.createDomainNamed("test name");
        Component component = activeObjectsHelper.createComponentNamed("test name");
        task.setComponent(component);
        Plan plan = activeObjectsHelper.createPlan("Name", "description", "time");
        PlanToDomainRelation relation = activeObjects.create(PlanToDomainRelation.class);
        relation.setDomain(domain);
        relation.setPlan(plan);
        relation.save();
        PlanToComponentRelation relation2 = activeObjects.create(PlanToComponentRelation.class);
        relation2.setPlan(plan);
        relation2.setComponent(component);
        relation2.save();

        service.clearDatabase();

        assertEquals(0, activeObjects.count(Domain.class));
        assertEquals(0, activeObjects.count(Plan.class));
        assertEquals(0, activeObjects.count(Component.class));
        assertEquals(0, activeObjects.count(SubTask.class));
        assertEquals(0, activeObjects.count(Task.class));
        assertEquals(0, activeObjects.count(PlanToComponentRelation.class));
        assertEquals(0, activeObjects.count(PlanToDomainRelation.class));
    }
}
