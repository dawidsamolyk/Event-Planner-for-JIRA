package ut.edu.uz.jira.event.planner.database;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToCategoryRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.TaskToComponentRelation;
import edu.uz.jira.event.planner.database.xml.model.*;
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

import java.util.Arrays;
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
        activeObjects.migrate(SubTaskToTaskRelation.class, TaskToComponentRelation.class, Category.class, Plan.class, Component.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToCategoryRelation.class);
        service = new ActiveObjectsService(activeObjects);
        activeObjects.flushAll();
        service.clearDatabase();
        activeObjectsHelper = new ActiveObjectsTestHelper(activeObjects);
    }

    @Test
    public void should_Not_Add_Any_With_Null_Configuration() throws ActiveObjectSavingException {
        exception.expect(ActiveObjectSavingException.class);
        service.addPlan(null);
        assertEquals(0, activeObjects.count(Category.class));

        exception.expect(ActiveObjectSavingException.class);
        service.addManyPlans(null);
        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void should_Not_Add_Any_With_Empty_Configuration() throws ActiveObjectSavingException {
        exception.expect(ActiveObjectSavingException.class);
        service.addPlan(PlanTemplate.createEmpty());
        assertEquals(0, activeObjects.count(Plan.class));

        EventPlanTemplates plans = new EventPlanTemplates();
        plans.setEventPlan(Arrays.asList(new PlanTemplate[]{PlanTemplate.createEmpty()}));
        exception.expect(ActiveObjectSavingException.class);
        service.addManyPlans(plans);
        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void should_Not_Add_Any_When_Confugiration_Is_Not_Fullfilled() throws ActiveObjectSavingException {
        PlanTemplate mockPlanConfig = mock(PlanTemplate.class);
        Mockito.when(mockPlanConfig.isFullfilled()).thenReturn(false);
        exception.expect(ActiveObjectSavingException.class);
        service.addPlan(mockPlanConfig);
        assertEquals(0, activeObjects.count(Plan.class));

        EventPlanTemplates plans = new EventPlanTemplates();
        plans.setEventPlan(Arrays.asList(new PlanTemplate[]{mockPlanConfig}));
        exception.expect(ActiveObjectSavingException.class);
        service.addManyPlans(plans);
        assertEquals(0, activeObjects.count(Plan.class));
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
        activeObjects.create(Category.class).save();

        List<Plan> result = service.get(Plan.class, Query.select());

        assertEquals(2, result.size());
    }

    @Test
    public void plan_Should_Not_Be_Added_If_Has_Fullfilled_Configuration_But_No_Related_Objects() throws ActiveObjectSavingException {
        PlanTemplate config = new PlanTemplate();
        config.setName("Test name");
        config.setDescription("Test description");

        exception.expect(ActiveObjectSavingException.class);
        service.addPlan(config);

        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void plan_Should_Be_Added_If_Has_Fullfilled_Configuration_And_Has_Related_Objects() throws ActiveObjectSavingException {
        Category category = activeObjectsHelper.createCategoryNamed("Test category");
        Component component = activeObjectsHelper.createComponentNamed("Test component");
        Task task = activeObjectsHelper.createTaskNamed("test");
        activeObjectsHelper.associate(component, task);
        PlanTemplate config = new PlanTemplate();
        config.setName("Test name");
        config.setDescription("Test description");
        config.setEventCategory(Arrays.asList(new EventCategory[]{EventCategory.createEmpty().fill(category)}));
        config.setComponent(Arrays.asList(new ComponentTemplate[]{ComponentTemplate.createEmpty().fill(component)}));

        service.addPlan(config);

        assertEquals(1, activeObjects.count(Plan.class));
    }

    @Test
    public void event_category_Should_Be_Added_If_Has_Fullfilled_Configuration() throws ActiveObjectSavingException {
        EventCategory category = new EventCategory();
        category.setName("Test name");

        Component component = activeObjectsHelper.createComponentNamed("Test component");
        Task task = activeObjectsHelper.createTaskNamed("test");
        activeObjectsHelper.associate(component, task);
        PlanTemplate plan = new PlanTemplate();
        plan.setName("Test name");
        plan.setDescription("Test description");
        plan.setEventCategory(Arrays.asList(category));
        plan.setComponent(Arrays.asList(new ComponentTemplate[]{ComponentTemplate.createEmpty().fill(component)}));

        service.addPlan(plan);

        assertEquals(1, activeObjects.count(Category.class));
    }

    @Test
    public void task_Should_Be_Added_If_Has_Fullfilled_Configuration_With_Related_Sub_Tasks() throws ActiveObjectSavingException {
        PlanTemplate config = new PlanTemplate();
        config.setName("Test name");
        TaskTemplate testTask = new TaskTemplate();
        testTask.setName("Test task");
        testTask.setNeededDaysBeforeEvent(5);
        ComponentTemplate component = new ComponentTemplate();
        component.setName("Test component");
        component.setTask(Arrays.asList(new TaskTemplate[]{testTask}));
        config.setComponent(Arrays.asList(new ComponentTemplate[]{component}));
        EventCategory eventCategory = EventCategory.createEmpty();
        eventCategory.setName("Test category");
        config.setEventCategory(Arrays.asList(new EventCategory[]{eventCategory}));

        service.addPlan(config);

        assertEquals(1, activeObjects.count(Task.class));
    }

    @Test
    public void component_Should_Be_Added_If_Has_Fullfilled_Configuration() throws ActiveObjectSavingException {
        PlanTemplate config = new PlanTemplate();
        config.setName("Test name");
        TaskTemplate testTask = new TaskTemplate();
        testTask.setName("Test task");
        testTask.setNeededDaysBeforeEvent(5);
        ComponentTemplate component = new ComponentTemplate();
        component.setName("Test component");
        component.setTask(Arrays.asList(new TaskTemplate[]{testTask}));
        config.setComponent(Arrays.asList(new ComponentTemplate[]{component}));
        EventCategory eventCategory = EventCategory.createEmpty();
        eventCategory.setName("Test category");
        config.setEventCategory(Arrays.asList(new EventCategory[]{eventCategory}));

        service.addPlan(config);

        assertEquals(1, activeObjects.count(Component.class));
    }

    @Test
    public void component_Should_Be_Added_If_Has_Fullfilled_Configuration_With_Related_Tasks() throws ActiveObjectSavingException {
        PlanTemplate config = new PlanTemplate();
        config.setName("Test name");
        TaskTemplate testTask = new TaskTemplate();
        testTask.setName("Test task");
        testTask.setNeededDaysBeforeEvent(5);
        ComponentTemplate component = new ComponentTemplate();
        component.setName("Test component");
        component.setTask(Arrays.asList(new TaskTemplate[]{testTask}));
        config.setComponent(Arrays.asList(new ComponentTemplate[]{component}));
        EventCategory eventCategory = EventCategory.createEmpty();
        eventCategory.setName("Test category");
        config.setEventCategory(Arrays.asList(new EventCategory[]{eventCategory}));

        service.addPlan(config);

        assertEquals(1, activeObjects.find(Component.class).length);
        Component[] createdTask = activeObjects.find(Component.class, Query.select().where(Component.NAME + " = ?", "Test component"));
        assertEquals(1, createdTask[0].getTasks().length);
    }

    @Test
    public void sub_Task_Should_Be_Added_If_Has_Fullfilled_Configuration() throws ActiveObjectSavingException {
        PlanTemplate config = new PlanTemplate();
        config.setName("Test name");
        TaskTemplate testTask = new TaskTemplate();
        testTask.setName("Test task");
        testTask.setNeededDaysBeforeEvent(5);
        SubTaskTemplate testSubTask = new SubTaskTemplate();
        testSubTask.setName("Test sub-task");
        testTask.setSubTask(Arrays.asList(new SubTaskTemplate[]{testSubTask}));
        ComponentTemplate component = new ComponentTemplate();
        component.setName("Test component");
        component.setTask(Arrays.asList(new TaskTemplate[]{testTask}));
        config.setComponent(Arrays.asList(new ComponentTemplate[]{component}));
        EventCategory eventCategory = EventCategory.createEmpty();
        eventCategory.setName("Test category");
        config.setEventCategory(Arrays.asList(new EventCategory[]{eventCategory}));

        service.addPlan(config);

        assertEquals(1, activeObjects.count(SubTask.class));
    }

    @Test
    public void plan_Should_Not_Be_Add_If_Cannot_Relate_It_With_Category() throws ActiveObjectSavingException {
        Component component = activeObjectsHelper.createComponentNamed("Test componnet");
        PlanTemplate config = new PlanTemplate();
        config.setName("Name");
        config.setDescription("Description");

        exception.expect(ActiveObjectSavingException.class);
        service.addPlan(config);

        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void plan_Should_Not_Be_Add_If_Cannot_Relate_It_With_Component() throws ActiveObjectSavingException {
        Category category = activeObjectsHelper.createCategoryNamed("test category");
        PlanTemplate config = new PlanTemplate();
        config.setName("Name");
        config.setDescription("Description");

        exception.expect(ActiveObjectSavingException.class);
        service.addPlan(config);

        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void component_Should_Not_Be_Add_If_Cannot_Relate_It_With_Tasks() throws ActiveObjectSavingException {
        PlanTemplate config = new PlanTemplate();
        config.setName("Test name");
        ComponentTemplate component = new ComponentTemplate();
        component.setName("Test component");
        config.setComponent(Arrays.asList(new ComponentTemplate[]{component}));
        EventCategory eventCategory = EventCategory.createEmpty();
        eventCategory.setName("Test category");
        config.setEventCategory(Arrays.asList(new EventCategory[]{eventCategory}));

        exception.expect(ActiveObjectSavingException.class);
        service.addPlan(config);

        assertEquals(0, activeObjects.count(Component.class));
    }

    @Test
    public void any_Entity_Should_Be_Deleted_By_Id() {
        Task task = activeObjectsHelper.createTask("Name", 0, 12);

        service.delete(Task.class, Integer.toString(task.getID()));

        assertEquals(0, activeObjects.count(Task.class));
    }

    @Test
    public void any_Entity_Related_With_Others_Should_Be_Deleted_By_Id() {
        Category category = activeObjectsHelper.createCategoryNamed("test name");
        Component component = activeObjectsHelper.createComponentNamed("test name");
        Plan plan = activeObjectsHelper.createPlan("Name", "description", 0, 12);
        PlanToCategoryRelation relation = activeObjects.create(PlanToCategoryRelation.class);
        relation.setCategory(category);
        relation.setPlan(plan);
        relation.save();
        PlanToComponentRelation relation2 = activeObjects.create(PlanToComponentRelation.class);
        relation2.setPlan(plan);
        relation2.setComponent(component);
        relation2.save();

        service.delete(Plan.class, Integer.toString(plan.getID()));

        assertEquals(0, activeObjects.count(Plan.class));
        assertEquals(0, activeObjects.count(PlanToComponentRelation.class));
        assertEquals(0, activeObjects.count(PlanToCategoryRelation.class));
        assertEquals(1, activeObjects.count(Category.class));
        assertEquals(1, activeObjects.count(Component.class));
    }

    @Test
    public void should_Clear_Database() {
        Task task = activeObjectsHelper.createTask("test name", 0, 13);
        SubTask subTask = activeObjectsHelper.createSubTaskNamed("name");
        activeObjectsHelper.associate(task, subTask);
        Category category = activeObjectsHelper.createCategoryNamed("test name");
        Component component = activeObjectsHelper.createComponentNamed("test name");
        activeObjectsHelper.associate(component, task);
        Plan plan = activeObjectsHelper.createPlan("Name", "description", 0, 14);
        PlanToCategoryRelation relation = activeObjects.create(PlanToCategoryRelation.class);
        relation.setCategory(category);
        relation.setPlan(plan);
        relation.save();
        PlanToComponentRelation relation2 = activeObjects.create(PlanToComponentRelation.class);
        relation2.setPlan(plan);
        relation2.setComponent(component);
        relation2.save();

        service.clearDatabase();

        assertEquals(0, activeObjects.count(Category.class));
        assertEquals(0, activeObjects.count(Plan.class));
        assertEquals(0, activeObjects.count(Component.class));
        assertEquals(0, activeObjects.count(SubTask.class));
        assertEquals(0, activeObjects.count(Task.class));
        assertEquals(0, activeObjects.count(PlanToComponentRelation.class));
        assertEquals(0, activeObjects.count(PlanToCategoryRelation.class));
        assertEquals(0, activeObjects.count(SubTaskToTaskRelation.class));
        assertEquals(0, activeObjects.count(TaskToComponentRelation.class));
    }
}
