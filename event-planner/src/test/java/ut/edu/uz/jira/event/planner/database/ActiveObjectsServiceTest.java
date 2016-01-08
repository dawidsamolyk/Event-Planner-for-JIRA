package ut.edu.uz.jira.event.planner.database;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import edu.uz.jira.event.planner.database.ActiveObjectsService;
import edu.uz.jira.event.planner.database.model.*;
import edu.uz.jira.event.planner.database.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.database.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.model.relation.TaskToComponentRelation;
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
        activeObjects.migrate(SubTaskToTaskRelation.class, TaskToComponentRelation.class, Domain.class, Plan.class, Component.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToDomainRelation.class);
        service = new ActiveObjectsService(activeObjects);
        activeObjects.flushAll();
        service.clearDatabase();
        activeObjectsHelper = new ActiveObjectsTestHelper(activeObjects);
    }

    @Test
    public void should_Not_Add_Any_With_Null_Configuration() {
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
    public void should_Not_Add_Any_With_Empty_Configuration() {
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
    public void should_Not_Add_Any_When_Confugiration_Is_Not_Fullfilled() {
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
    public void should_Not_Get_Any_If_Input_Type_Is_Null() {
        List<RawEntity<Integer>> result = service.get(null, Query.select());

        assertTrue(result.isEmpty());
    }

    @Test
    public void should_Return_Array_Of_Database_Objects_Of_Specified_Type() {
        activeObjects.create(Plan.class).save();
        activeObjects.create(Plan.class).save();
        activeObjects.create(Task.class).save();
        activeObjects.create(Domain.class).save();

        List<Plan> result = service.get(Plan.class, Query.select());

        assertEquals(2, result.size());
    }

    @Test
    public void plan_Should_Not_Be_Added_If_Has_Fullfilled_Configuration_But_No_Related_Objects() {
        EventPlanRestManager.Configuration config = new EventPlanRestManager.Configuration();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTime(689);
        config.setDomains(new String[]{"Test domain"});
        config.setComponents(new String[]{"Test component"});

        service.addFrom(config);

        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void plan_Should_Be_Added_If_Has_Fullfilled_Configuration_And_Has_Related_Objects() {
        Domain domain = activeObjectsHelper.createDomainNamed("Test domain");
        Component component = activeObjectsHelper.createComponentNamed("Test component");
        EventPlanRestManager.Configuration config = new EventPlanRestManager.Configuration();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTime(123);
        config.setDomains(new String[]{domain.getName()});
        config.setComponents(new String[]{component.getName()});

        service.addFrom(config);

        assertEquals(1, activeObjects.count(Plan.class));
    }

    @Test
    public void domain_Should_Be_Added_If_Has_Fullfilled_Configuration() {
        EventDomainRestManager.Configuration configuration = new EventDomainRestManager.Configuration();
        configuration.setName("Test name");
        configuration.setDescription("Test description");

        service.addFrom(configuration);

        assertEquals(1, activeObjects.count(Domain.class));
    }

    @Test
    public void task_Should_Be_Added_If_Has_Fullfilled_Configuration_With_Related_Sub_Tasks() {
        SubTask firstSubTask = activeObjectsHelper.createSubTaskNamed("Test 1");
        SubTask secondSubTask = activeObjectsHelper.createSubTaskNamed("Test 2");
        EventTaskRestManager.Configuration config = new EventTaskRestManager.Configuration();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTime(123);
        config.setSubtasks(new String[]{firstSubTask.getName(), secondSubTask.getName()});

        service.addFrom(config);

        assertEquals(1, activeObjects.find(Task.class).length);
        Task[] createdTask = activeObjects.find(Task.class, Query.select().where(Task.NAME + " = ?", config.getName()));
        assertEquals(2, createdTask[0].getSubTasks().length);
    }

    @Test
    public void component_Should_Be_Added_If_Has_Fullfilled_Configuration() {
        Task task = activeObjectsHelper.createTaskNamed("Test");
        EventComponentRestManager.Configuration config = new EventComponentRestManager.Configuration();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTasks(new String[]{task.getName()});

        service.addFrom(config);

        assertEquals(1, activeObjects.count(Component.class));
    }

    @Test
    public void component_Should_Be_Added_If_Has_Fullfilled_Configuration_With_Related_Tasks() {
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
    public void sub_Task_Should_Be_Added_If_Has_Fullfilled_Configuration() {
        EventSubTaskRestManager.Configuration config = new EventSubTaskRestManager.Configuration();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTime(123);

        service.addFrom(config);

        assertEquals(1, activeObjects.count(SubTask.class));
    }

    @Test
    public void plan_Should_Not_Be_Add_If_Cannot_Relate_It_With_Domain() {
        Component component = activeObjectsHelper.createComponentNamed("Test componnet");
        EventPlanRestManager.Configuration config = new EventPlanRestManager.Configuration();
        config.setName("Name");
        config.setDescription("Description");
        config.setTime(123);
        config.setDomains(new String[]{"Nonexistent domain"});
        config.setComponents(new String[]{component.getName()});

        service.addFrom(config);

        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void plan_Should_Not_Be_Add_If_Cannot_Relate_It_With_Component() {
        Domain domain = activeObjectsHelper.createDomainNamed("test domain");
        EventPlanRestManager.Configuration config = new EventPlanRestManager.Configuration();
        config.setName("Name");
        config.setDescription("Description");
        config.setTime(123);
        config.setComponents(new String[]{"Nonexistent component"});
        config.setDomains(new String[]{domain.getName()});

        service.addFrom(config);

        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void component_Should_Not_Be_Add_If_Cannot_Relate_It_With_Tasks() {
        EventComponentRestManager.Configuration config = new EventComponentRestManager.Configuration();
        config.setName("Name");
        config.setDescription("Description");
        config.setTasks(new String[]{"Nonexistent task"});

        service.addFrom(config);

        assertEquals(0, activeObjects.count(Component.class));
    }

    @Test
    public void task_Should_Not_Be_Add_If_Cannot_Relate_It_With_Sub_Tasks() {
        EventTaskRestManager.Configuration config = new EventTaskRestManager.Configuration();
        config.setName("Name");
        config.setDescription("Description");
        config.setTime(123);
        config.setSubtasks(new String[]{"Nonexistent subtask"});

        service.addFrom(config);

        assertEquals(0, activeObjects.count(Task.class));
    }

    @Test
    public void any_Entity_Should_Be_Deleted_By_Id() {
        Task task = activeObjectsHelper.createTask("Name", 123);

        service.delete(Task.class, Integer.toString(task.getID()));

        assertEquals(0, activeObjects.count(Task.class));
    }

    @Test
    public void any_Entity_Related_With_Others_Should_Be_Deleted_By_Id() {
        Domain domain = activeObjectsHelper.createDomainNamed("test name");
        Component component = activeObjectsHelper.createComponentNamed("test name");
        Plan plan = activeObjectsHelper.createPlan("Name", "description", 123);
        PlanToDomainRelation relation = activeObjects.create(PlanToDomainRelation.class);
        relation.setDomain(domain);
        relation.setPlan(plan);
        relation.save();
        PlanToComponentRelation relation2 = activeObjects.create(PlanToComponentRelation.class);
        relation2.setPlan(plan);
        relation2.setComponent(component);
        relation2.save();

        service.delete(Plan.class, Integer.toString(plan.getID()));

        assertEquals(0, activeObjects.count(Plan.class));
        assertEquals(0, activeObjects.count(PlanToComponentRelation.class));
        assertEquals(0, activeObjects.count(PlanToDomainRelation.class));
        assertEquals(1, activeObjects.count(Domain.class));
        assertEquals(1, activeObjects.count(Component.class));
    }

    @Test
    public void should_Clear_Database() {
        Task task = activeObjectsHelper.createTask("test name", 123);
        SubTask subTask = activeObjectsHelper.createSubTask("name", 123);
        activeObjectsHelper.associate(task, subTask);
        Domain domain = activeObjectsHelper.createDomainNamed("test name");
        Component component = activeObjectsHelper.createComponentNamed("test name");
        activeObjectsHelper.associate(component, task);
        Plan plan = activeObjectsHelper.createPlan("Name", "description", 123);
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
        assertEquals(0, activeObjects.count(SubTaskToTaskRelation.class));
        assertEquals(0, activeObjects.count(TaskToComponentRelation.class));
    }
}
