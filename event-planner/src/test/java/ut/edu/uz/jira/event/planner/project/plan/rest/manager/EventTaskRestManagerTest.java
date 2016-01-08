package ut.edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import edu.uz.jira.event.planner.database.ActiveObjectsService;
import edu.uz.jira.event.planner.database.model.*;
import edu.uz.jira.event.planner.database.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.database.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.model.relation.TaskToComponentRelation;
import edu.uz.jira.event.planner.project.plan.rest.EventRestConfiguration;
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
public class EventTaskRestManagerTest {
    private EntityManager entityManager;
    private MockHttpServletRequest mockRequest;
    private UserManager mockUserManager;
    private TransactionTemplate mockTransactionTemplateForGet;
    private TransactionTemplate mockTransactionTemplateForPut;
    private ActiveObjectsService planService;
    private ActiveObjects activeObjects;
    private EventRestConfiguration[] transactionResult;
    private ActiveObjectsTestHelper testHelper;

    @Before
    public void setUp() {
        mockRequest = new MockHttpServletRequest();

        mockUserManager = mock(UserManager.class);
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(mock(UserProfile.class));
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(true);

        mockTransactionTemplateForGet = mock(TransactionTemplate.class);
        Mockito.when(mockTransactionTemplateForGet.execute(Mockito.any(TransactionCallback.class))).thenAnswer(new Answer<EventRestConfiguration[]>() {
            @Override
            public EventRestConfiguration[] answer(InvocationOnMock invocation) throws Throwable {
                TransactionCallback<EventRestConfiguration[]> callback = (TransactionCallback<EventRestConfiguration[]>) invocation.getArguments()[0];
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
        activeObjects.migrate(SubTaskToTaskRelation.class, TaskToComponentRelation.class, Domain.class, Plan.class, Component.class,  SubTask.class, Task.class, PlanToComponentRelation.class, PlanToDomainRelation.class);
        planService = new ActiveObjectsService(activeObjects);
        planService.clearDatabase();

        testHelper = new ActiveObjectsTestHelper(activeObjects);
    }

    @Test
    public void should_Get_Task_From_Database() throws SQLException {
        String testName = "Test name";
        int testTime = 123;
        testHelper.createTask(testName, testTime);
        EventTaskRestManager fixture = new EventTaskRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        fixture.get(mockRequest);

        EventTaskRestManager.Configuration expected = new EventTaskRestManager.Configuration();
        expected.setName(testName);
        expected.setTime(testTime);
        assertEquals(expected, transactionResult[0]);
    }

    @Test
    public void should_Get_Many_Tasks_From_Database() throws SQLException {
        testHelper.createTask("Task 1", 123);
        testHelper.createTask("Task 2", 123);
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
        EventTaskRestManager.Configuration configuration = new EventTaskRestManager.Configuration();
        configuration.setName("Test name");
        configuration.setDescription("Test description");
        configuration.setTime(123);

        Response result = fixture.post(configuration, mockRequest);

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), result.getStatus());
    }

    @Test
    public void on_Delete_should_remove_entity_with_specified_id() throws SQLException {
        Task task = testHelper.createTaskNamed("test name");
        EventTaskRestManager fixture = new EventTaskRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response response = fixture.delete(Integer.toString(task.getID()), mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(0, activeObjects.count(Domain.class));
    }

    @Test
    public void on_Post_should_get_entity_with_specified_id() throws SQLException {
        Task task = testHelper.createTaskNamed("test name");
        EventTaskRestManager fixture = new EventTaskRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response response = fixture.post(Integer.toString(task.getID()), mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        EventRestConfiguration expected = EventTaskRestManager.Configuration.createEmpty().fill(task);
        assertEquals(expected, transactionResult[0]);
    }
}
