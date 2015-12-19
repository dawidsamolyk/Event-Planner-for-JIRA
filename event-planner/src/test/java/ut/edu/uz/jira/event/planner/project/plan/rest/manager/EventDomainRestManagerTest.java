package ut.edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import edu.uz.jira.event.planner.project.plan.ActiveObjectsService;
import edu.uz.jira.event.planner.project.plan.model.*;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.project.plan.rest.EventRestConfiguration;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventComponentRestManager;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventDomainRestManager;
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
public class EventDomainRestManagerTest {
    private EntityManager entityManager;
    private MockHttpServletRequest mockRequest;
    private UserManager mockUserManager;
    private TransactionTemplate mockTransactionTemplateForGet;
    private TransactionTemplate mockTransactionTemplateForPut;
    private ActiveObjectsService planService;
    private ActiveObjects activeObjects;
    private EventRestConfiguration[] transactionResult;
    private ActiveObjectsTestHelper testHelper;

    private EventDomainRestManager.Configuration getEmptyDomain() {
        return EventDomainRestManager.Configuration.createEmpty();
    }

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
                TransactionCallback<EventRestConfiguration[]> callback = (TransactionCallback) invocation.getArguments()[0];
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
        activeObjects.migrate(Domain.class, Plan.class, Component.class, Plan.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToDomainRelation.class);
        planService = new ActiveObjectsService(activeObjects);
        planService.clearDatabase();

        testHelper = new ActiveObjectsTestHelper(activeObjects);
    }

    @Test
    public void shouldGetComponentFromDatabase() throws SQLException {
        String testName = "Test name";
        String testDescription = "Test description";
        testHelper.createComponent(testName, testDescription);
        EventComponentRestManager fixture = new EventComponentRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        fixture.get(null, mockRequest);

        EventComponentRestManager.Configuration expected = new EventComponentRestManager.Configuration();
        expected.setName(testName);
        expected.setDescription(testDescription);
        assertEquals(expected, transactionResult[0]);
    }

    @Test
    public void shouldGetManyComponentsFromDatabase() throws SQLException {
        testHelper.createComponent("Component 1", "Description");
        testHelper.createComponent("Component 2", "Description");
        EventComponentRestManager fixture = new EventComponentRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        fixture.get(null, mockRequest);

        assertTrue(transactionResult.length == 2);
    }

    @Test
    public void shouldGetEmptyComponentArrayWhenThereIsNoDomainsInDatabase() {
        EventComponentRestManager fixture = new EventComponentRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        fixture.get(null, mockRequest);

        assertTrue(transactionResult.length == 0);
    }

    @Test
    public void shouldPutNewComponent() {
        Task firstTask = testHelper.createTaskNamed("Test task1");
        Task secondTask = testHelper.createTaskNamed("Test task2");
        EventComponentRestManager fixture = new EventComponentRestManager(mockUserManager, mockTransactionTemplateForPut, planService);
        EventComponentRestManager.Configuration configuration = new EventComponentRestManager.Configuration();
        configuration.setName("Test name");
        configuration.setDescription("Test description");
        configuration.setTasks(new String[]{firstTask.getName(), secondTask.getName()});

        Response result = fixture.put(configuration, mockRequest);

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), result.getStatus());
    }
}
