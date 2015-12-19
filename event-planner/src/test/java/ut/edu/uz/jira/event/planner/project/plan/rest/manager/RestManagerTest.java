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
import edu.uz.jira.event.planner.project.plan.rest.manager.EventDomainRestManager;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventPlanRestManager;
import edu.uz.jira.event.planner.project.plan.rest.manager.RestManager;
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
import static org.mockito.Mockito.mock;

@Transactional
@RunWith(ActiveObjectsJUnitRunner.class)
@Jdbc(Hsql.class)
@NameConverters
public class RestManagerTest {
    private EntityManager entityManager;
    private MockHttpServletRequest mockRequest;
    private UserManager mockUserManager;
    private TransactionTemplate mockTransactionTemplateForGet;
    private TransactionTemplate mockTransactionTemplateForPut;
    private ActiveObjectsService planService;
    private ActiveObjects activeObjects;
    private EventRestConfiguration[] doGetTransactionResult;
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
        Mockito.when(mockTransactionTemplateForGet.execute(Mockito.any(TransactionCallback.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TransactionCallback<EventRestConfiguration[]> callback = (TransactionCallback) invocation.getArguments()[0];
                doGetTransactionResult = callback.doInTransaction();
                return doGetTransactionResult;
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
    public void on_Get_Should_Response_Unauthorized_When_User_Is_Null() {
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(null);
        RestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response result = fixture.post(null, mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void on_Put_Should_Response_Unauthorized_When_User_Is_Null() {
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(null);
        RestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response result = fixture.put(getEmptyDomain(), mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void on_Getshould_Response_Unauthorized_When_User_Is_Not_Admin() {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        RestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response result = fixture.post(null, mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void on_Putshould_Response_Unauthorized_When_User_Is_Not_Admin() {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        RestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response result = fixture.put(getEmptyDomain(), mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void on_Putshould_Response_No_Conten_When_Resource_Is_Null() {
        RestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response result = fixture.put(null, mockRequest);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), result.getStatus());
    }


    @Test
    public void should_Not_Put_Empty_Configuration() {
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response result = fixture.put(getEmptyDomain(), mockRequest);

        assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), result.getStatus());
    }

    @Test
    public void on_Put_Should_Not_Accept_When_Trying_To_Put_Configuration_Of_Invalid_Resource() throws SQLException {
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplateForPut, planService);
        EventPlanRestManager.Configuration invalidConfig = new EventPlanRestManager.Configuration();
        invalidConfig.setName("Test name");
        invalidConfig.setDescription("Test description");
        invalidConfig.setDomains(new String[]{"Test domain"});
        invalidConfig.setComponents(new String[]{"Test component"});
        invalidConfig.setTime("Test time");

        Response result = fixture.put(invalidConfig, mockRequest);

        assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), result.getStatus());
    }

    @Test
    public void on_Post_Should_Return_Only_One_Resource_With_Specified_Id() throws SQLException {
        Domain firstDomain = testHelper.createDomain("name", "descr");
        testHelper.createDomain("test", "test");

        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response result = fixture.post(Integer.toString(firstDomain.getID()), mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals(1, doGetTransactionResult.length);
        assertEquals(firstDomain.getName(), ((EventDomainRestManager.Configuration) doGetTransactionResult[0]).getName());
    }

    @Test
    public void on_Get_Should_Return_All_Resources() throws SQLException {
        Domain firstDomain = testHelper.createDomain("name", "descr");
        Domain secondDomain = testHelper.createDomain("test", "test");

        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals(2, doGetTransactionResult.length);
        assertEquals(firstDomain.getName(), ((EventDomainRestManager.Configuration) doGetTransactionResult[0]).getName());
        assertEquals(secondDomain.getName(), ((EventDomainRestManager.Configuration) doGetTransactionResult[1]).getName());
    }
}
