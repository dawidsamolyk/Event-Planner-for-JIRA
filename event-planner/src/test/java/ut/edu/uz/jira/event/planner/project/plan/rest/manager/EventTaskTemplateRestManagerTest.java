package ut.edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.SubTask;
import edu.uz.jira.event.planner.database.active.objects.model.Task;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToCategoryRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.TaskToComponentRelation;
import edu.uz.jira.event.planner.database.xml.model.TaskTemplate;
import edu.uz.jira.event.planner.project.plan.rest.ActiveObjectWrapper;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventTaskRestManager;
import net.java.ao.EntityManager;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.Hsql;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ut.helpers.ActiveObjectsTestHelper;
import ut.helpers.TestActiveObjects;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@Transactional
@RunWith(ActiveObjectsJUnitRunner.class)
@Jdbc(Hsql.class)
@NameConverters
public class EventTaskTemplateRestManagerTest {
    private EntityManager entityManager;
    private MockHttpServletRequest mockRequest;
    private UserManager mockUserManager;
    private TransactionTemplate mockTransactionTemplateForGet;
    private TransactionTemplate mockTransactionTemplateForPut;
    private ActiveObjectsService planService;
    private ActiveObjects activeObjects;
    private ActiveObjectWrapper[] transactionResult;
    private ActiveObjectsTestHelper testHelper;

    @Before
    public void setUp() {
        mockRequest = new MockHttpServletRequest();

        mockUserManager = mock(UserManager.class);
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(mock(UserProfile.class));
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(true);

        mockTransactionTemplateForGet = mock(TransactionTemplate.class);
        Mockito.when(mockTransactionTemplateForGet.execute(Mockito.any(TransactionCallback.class))).thenAnswer(new Answer<ActiveObjectWrapper[]>() {
            @Override
            public ActiveObjectWrapper[] answer(InvocationOnMock invocation) throws Throwable {
                TransactionCallback<ActiveObjectWrapper[]> callback = (TransactionCallback<ActiveObjectWrapper[]>) invocation.getArguments()[0];
                transactionResult = callback.doInTransaction();
                return transactionResult;
            }
        });

        mockTransactionTemplateForPut = mock(TransactionTemplate.class);
        Mockito.when(mockTransactionTemplateForPut.execute(Mockito.any(TransactionCallback.class))).thenAnswer(new Answer<Response>() {
            @Override
            public Response answer(InvocationOnMock invocation) throws Throwable {
                TransactionCallback<Response> callback = (TransactionCallback) invocation.getArguments()[0];
                return callback.doInTransaction();
            }
        });

        activeObjects = mock(ActiveObjects.class);
        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.migrate(SubTaskToTaskRelation.class, TaskToComponentRelation.class, Category.class, Plan.class, Component.class,  SubTask.class, Task.class, PlanToComponentRelation.class, PlanToCategoryRelation.class);
        planService = new ActiveObjectsService(activeObjects);
        planService.clearDatabase();

        testHelper = new ActiveObjectsTestHelper(activeObjects);
    }

    @Test
    public void should_Get_Task_From_Database() throws SQLException {
        String testName = "Test name";
        int testDays = 123;
        testHelper.createTask(testName, 0, testDays);
        EventTaskRestManager fixture = new EventTaskRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        fixture.get(mockRequest);

        TaskTemplate expected = new TaskTemplate();
        expected.setName(testName);
        expected.setNeededDaysBeforeEvent(testDays);
        assertEquals(expected, transactionResult[0]);
    }

    @Test
    public void should_Get_Many_Tasks_From_Database() throws SQLException {
        testHelper.createTask("Task 1", 0, 2);
        testHelper.createTask("Task 2", 0, 5);
        EventTaskRestManager fixture = new EventTaskRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        fixture.get(mockRequest);

        assertTrue(transactionResult.length == 2);
    }

    @Test
    public void should_Get_Empty_Tasks_Array_When_There_Is_No_Tasks_In_Database() {
        EventTaskRestManager fixture = new EventTaskRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        fixture.get(mockRequest);

        assertTrue(transactionResult.length == 0);
    }

    @Test
    public void should_Post_New_Task() {
        EventTaskRestManager fixture = new EventTaskRestManager(mockUserManager, mockTransactionTemplateForPut, planService);
        TaskTemplate configuration = new TaskTemplate();
        configuration.setName("Test name");
        configuration.setDescription("Test description");
        configuration.setNeededDaysBeforeEvent(12);

        Response result = fixture.post(configuration, mockRequest);

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), result.getStatus());
    }

    @Test
    public void on_Delete_should_remove_entity_with_specified_id() throws SQLException {
        Task task = testHelper.createTaskNamed("test name");
        EventTaskRestManager fixture = new EventTaskRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response response = fixture.delete(Integer.toString(task.getID()), mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(0, activeObjects.count(Category.class));
    }

    @Test
    public void on_Post_should_get_entity_with_specified_id() throws SQLException {
        Task task = testHelper.createTaskNamed("test name");
        EventTaskRestManager fixture = new EventTaskRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response response = fixture.post(Integer.toString(task.getID()), mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        ActiveObjectWrapper expected = TaskTemplate.createEmpty().fill(task);
        assertEquals(expected, transactionResult[0]);
    }
}
