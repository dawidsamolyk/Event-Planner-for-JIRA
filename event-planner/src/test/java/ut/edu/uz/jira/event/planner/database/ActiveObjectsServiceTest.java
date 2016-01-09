package ut.edu.uz.jira.event.planner.database;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.TaskToComponentRelation;
import edu.uz.jira.event.planner.database.importer.xml.model.EventPlan;
import edu.uz.jira.event.planner.exception.ActiveObjectSavingException;
import net.java.ao.EntityManager;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.Hsql;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
    @Rule
    public ExpectedException exception = ExpectedException.none();

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
    public void should_Not_Add_Any_With_Null_Configuration() throws ActiveObjectSavingException {
        exception.expect(ActiveObjectSavingException.class);
        service.addFrom((edu.uz.jira.event.planner.database.importer.xml.model.Domain) null);
        assertEquals(0, activeObjects.count(Domain.class));

        exception.expect(ActiveObjectSavingException.class);
        service.addFrom((edu.uz.jira.event.planner.database.importer.xml.model.EventPlan) null);
        assertEquals(0, activeObjects.count(Plan.class));

        exception.expect(ActiveObjectSavingException.class);
        service.addFrom((edu.uz.jira.event.planner.database.importer.xml.model.Component) null);
        assertEquals(0, activeObjects.count(Component.class));

        exception.expect(ActiveObjectSavingException.class);
        service.addFrom((edu.uz.jira.event.planner.database.importer.xml.model.SubTask) null);
        assertEquals(0, activeObjects.count(SubTask.class));

        exception.expect(ActiveObjectSavingException.class);
        service.addFrom((edu.uz.jira.event.planner.database.importer.xml.model.Task) null);
        assertEquals(0, activeObjects.count(Task.class));
    }

    @Test
    public void should_Not_Add_Any_With_Empty_Configuration() throws ActiveObjectSavingException {
        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(edu.uz.jira.event.planner.database.importer.xml.model.Domain.createEmpty());
        assertEquals(0, activeObjects.count(Domain.class));

        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(EventPlan.createEmpty());
        assertEquals(0, activeObjects.count(Plan.class));

        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(edu.uz.jira.event.planner.database.importer.xml.model.Component.createEmpty());
        assertEquals(0, activeObjects.count(Component.class));

        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(edu.uz.jira.event.planner.database.importer.xml.model.SubTask.createEmpty());
        assertEquals(0, activeObjects.count(SubTask.class));

        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(edu.uz.jira.event.planner.database.importer.xml.model.Task.createEmpty());
        assertEquals(0, activeObjects.count(Task.class));
    }

    @Test
    public void should_Not_Add_Any_When_Confugiration_Is_Not_Fullfilled() throws ActiveObjectSavingException {
        edu.uz.jira.event.planner.database.importer.xml.model.Component mockDomainConfig = mock(edu.uz.jira.event.planner.database.importer.xml.model.Component.class);
        Mockito.when(mockDomainConfig.isFullfilled()).thenReturn(false);
        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(mockDomainConfig);
        assertEquals(0, activeObjects.count(Domain.class));

        EventPlan mockPlanConfig = mock(EventPlan.class);
        Mockito.when(mockPlanConfig.isFullfilled()).thenReturn(false);
        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(mockPlanConfig);
        assertEquals(0, activeObjects.count(Plan.class));

        edu.uz.jira.event.planner.database.importer.xml.model.Component mockComponentConfig = mock(edu.uz.jira.event.planner.database.importer.xml.model.Component.class);
        Mockito.when(mockComponentConfig.isFullfilled()).thenReturn(false);
        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(mockComponentConfig);
        assertEquals(0, activeObjects.count(Component.class));

        edu.uz.jira.event.planner.database.importer.xml.model.SubTask mockSubTaskConfig = mock(edu.uz.jira.event.planner.database.importer.xml.model.SubTask.class);
        Mockito.when(mockSubTaskConfig.isFullfilled()).thenReturn(false);
        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(mockComponentConfig);
        assertEquals(0, activeObjects.count(SubTask.class));

        edu.uz.jira.event.planner.database.importer.xml.model.Task mockTaskConfig = mock(edu.uz.jira.event.planner.database.importer.xml.model.Task.class);
        Mockito.when(mockTaskConfig.isFullfilled()).thenReturn(false);
        exception.expect(ActiveObjectSavingException.class);
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
    public void plan_Should_Not_Be_Added_If_Has_Fullfilled_Configuration_But_No_Related_Objects() throws ActiveObjectSavingException {
        EventPlan config = new EventPlan();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setNeededDays(689);
        config.setDomainsNames(new String[]{"Test domain"});
        config.setComponentsNames(new String[]{"Test component"});

        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(config);

        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void plan_Should_Be_Added_If_Has_Fullfilled_Configuration_And_Has_Related_Objects() throws ActiveObjectSavingException {
        Domain domain = activeObjectsHelper.createDomainNamed("Test domain");
        Component component = activeObjectsHelper.createComponentNamed("Test component");
        EventPlan config = new EventPlan();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setNeededDays(123);
        config.setDomainsNames(new String[]{domain.getName()});
        config.setComponentsNames(new String[]{component.getName()});

        service.addFrom(config);

        assertEquals(1, activeObjects.count(Plan.class));
    }

    @Test
    public void domain_Should_Be_Added_If_Has_Fullfilled_Configuration() throws ActiveObjectSavingException {
        edu.uz.jira.event.planner.database.importer.xml.model.Domain configuration = new edu.uz.jira.event.planner.database.importer.xml.model.Domain();
        configuration.setName("Test name");
        configuration.setDescription("Test description");

        service.addFrom(configuration);

        assertEquals(1, activeObjects.count(Domain.class));
    }

    @Test
    public void task_Should_Be_Added_If_Has_Fullfilled_Configuration_With_Related_Sub_Tasks() throws ActiveObjectSavingException {
        SubTask firstSubTask = activeObjectsHelper.createSubTaskNamed("Test 1");
        SubTask secondSubTask = activeObjectsHelper.createSubTaskNamed("Test 2");
        edu.uz.jira.event.planner.database.importer.xml.model.Task config = new edu.uz.jira.event.planner.database.importer.xml.model.Task();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setNeededDays(123);
        config.setSubTasksNames(new String[]{firstSubTask.getName(), secondSubTask.getName()});

        service.addFrom(config);

        assertEquals(1, activeObjects.find(Task.class).length);
        Task[] createdTask = activeObjects.find(Task.class, Query.select().where(Task.NAME + " = ?", config.getName()));
        assertEquals(2, createdTask[0].getSubTasks().length);
    }

    @Test
    public void component_Should_Be_Added_If_Has_Fullfilled_Configuration() throws ActiveObjectSavingException {
        Task task = activeObjectsHelper.createTaskNamed("Test");
        edu.uz.jira.event.planner.database.importer.xml.model.Component config = new edu.uz.jira.event.planner.database.importer.xml.model.Component();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTasksNames(new String[]{task.getName()});

        service.addFrom(config);

        assertEquals(1, activeObjects.count(Component.class));
    }

    @Test
    public void component_Should_Be_Added_If_Has_Fullfilled_Configuration_With_Related_Tasks() throws ActiveObjectSavingException {
        Task firstTask = activeObjectsHelper.createTaskNamed("Test 1");
        Task secondTask = activeObjectsHelper.createTaskNamed("Test 2");
        edu.uz.jira.event.planner.database.importer.xml.model.Component config = new edu.uz.jira.event.planner.database.importer.xml.model.Component();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setTasksNames(new String[]{firstTask.getName(), secondTask.getName()});

        service.addFrom(config);

        assertEquals(1, activeObjects.find(Component.class).length);
        Component[] createdTask = activeObjects.find(Component.class, Query.select().where(Component.NAME + " = ?", config.getName()));
        assertEquals(2, createdTask[0].getTasks().length);
    }

    @Test
    public void sub_Task_Should_Be_Added_If_Has_Fullfilled_Configuration() throws ActiveObjectSavingException {
        edu.uz.jira.event.planner.database.importer.xml.model.SubTask config = new edu.uz.jira.event.planner.database.importer.xml.model.SubTask();
        config.setName("Test name");
        config.setDescription("Test description");

        service.addFrom(config);

        assertEquals(1, activeObjects.count(SubTask.class));
    }

    @Test
    public void plan_Should_Not_Be_Add_If_Cannot_Relate_It_With_Domain() throws ActiveObjectSavingException {
        Component component = activeObjectsHelper.createComponentNamed("Test componnet");
        EventPlan config = new EventPlan();
        config.setName("Name");
        config.setDescription("Description");
        config.setNeededDays(123);
        config.setDomainsNames(new String[]{"Nonexistent domain"});
        config.setComponentsNames(new String[]{component.getName()});

        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(config);

        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void plan_Should_Not_Be_Add_If_Cannot_Relate_It_With_Component() throws ActiveObjectSavingException {
        Domain domain = activeObjectsHelper.createDomainNamed("test domain");
        EventPlan config = new EventPlan();
        config.setName("Name");
        config.setDescription("Description");
        config.setNeededDays(123);
        config.setComponentsNames(new String[]{"Nonexistent component"});
        config.setDomainsNames(new String[]{domain.getName()});

        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(config);

        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void component_Should_Not_Be_Add_If_Cannot_Relate_It_With_Tasks() throws ActiveObjectSavingException {
        edu.uz.jira.event.planner.database.importer.xml.model.Component config = new edu.uz.jira.event.planner.database.importer.xml.model.Component();
        config.setName("Name");
        config.setDescription("Description");
        config.setTasksNames(new String[]{"Nonexistent task"});

        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(config);

        assertEquals(0, activeObjects.count(Component.class));
    }

    @Test
    public void task_Should_Not_Be_Add_If_Cannot_Relate_It_With_Sub_Tasks() throws ActiveObjectSavingException {
        edu.uz.jira.event.planner.database.importer.xml.model.Task config = new edu.uz.jira.event.planner.database.importer.xml.model.Task();
        config.setName("Name");
        config.setDescription("Description");
        config.setNeededDays(123);
        config.setSubTasksNames(new String[]{"Nonexistent subtask"});

        exception.expect(ActiveObjectSavingException.class);
        service.addFrom(config);

        assertEquals(0, activeObjects.count(Task.class));
    }

    @Test
    public void any_Entity_Should_Be_Deleted_By_Id() {
        Task task = activeObjectsHelper.createTask("Name", 0, 12);

        service.delete(Task.class, Integer.toString(task.getID()));

        assertEquals(0, activeObjects.count(Task.class));
    }

    @Test
    public void any_Entity_Related_With_Others_Should_Be_Deleted_By_Id() {
        Domain domain = activeObjectsHelper.createDomainNamed("test name");
        Component component = activeObjectsHelper.createComponentNamed("test name");
        Plan plan = activeObjectsHelper.createPlan("Name", "description", 0, 12);
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
        Task task = activeObjectsHelper.createTask("test name", 0, 13);
        SubTask subTask = activeObjectsHelper.createSubTaskNamed("name");
        activeObjectsHelper.associate(task, subTask);
        Domain domain = activeObjectsHelper.createDomainNamed("test name");
        Component component = activeObjectsHelper.createComponentNamed("test name");
        activeObjectsHelper.associate(component, task);
        Plan plan = activeObjectsHelper.createPlan("Name", "description", 0, 14);
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
