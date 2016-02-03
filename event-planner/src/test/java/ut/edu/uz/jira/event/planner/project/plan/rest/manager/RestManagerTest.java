package ut.edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToCategoryRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.TaskToComponentRelation;
import edu.uz.jira.event.planner.database.xml.model.EventCategory;
import edu.uz.jira.event.planner.database.xml.model.PlanTemplate;
import edu.uz.jira.event.planner.project.plan.rest.ActiveObjectWrapper;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventPlanRestManager;
import edu.uz.jira.event.planner.util.text.Internationalization;
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
    private ActiveObjectWrapper[] doGetTransactionResult;
    private ActiveObjectsTestHelper testHelper;
    private I18nResolver mocki18n;
    private static final String PROJECT_VERSION_NAME = "Event Deadline";

    @Before
    public void setUp() {
        mockRequest = new MockHttpServletRequest();

        mockUserManager = mock(UserManager.class);
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(mock(UserProfile.class));
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(true);

        mocki18n = mock(I18nResolver.class);
        Mockito.when(mocki18n.getText(Internationalization.PROJECT_VERSION_NAME)).thenReturn(PROJECT_VERSION_NAME);

        mockTransactionTemplateForGet = mock(TransactionTemplate.class);
        Mockito.when(mockTransactionTemplateForGet.execute(Mockito.any(TransactionCallback.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TransactionCallback<ActiveObjectWrapper[]> callback = (TransactionCallback) invocation.getArguments()[0];
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
        activeObjects.migrate(SubTaskToTaskRelation.class, TaskToComponentRelation.class, Category.class, Plan.class, Component.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToCategoryRelation.class);
        planService = new ActiveObjectsService(activeObjects);
        planService.clearDatabase();

        testHelper = new ActiveObjectsTestHelper(activeObjects);
    }

    @Test
    public void on_Delete_Should_Response_Unauthorized_When_User_Is_Null() {
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(null);
        EventPlanRestManager fixture = getFixture();

        Response result = fixture.delete("10", mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    private EventPlanRestManager getFixture() {
        return new EventPlanRestManager(mockUserManager, mockTransactionTemplateForGet, planService, mocki18n);
    }

    @Test
    public void on_Get_should_Response_Unauthorized_When_User_Is_Not_Admin() {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        EventPlanRestManager fixture = getFixture();

        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void on_Delete_Should_Response_Unauthorized_When_User_Is_Not_Admin() {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        EventPlanRestManager fixture = getFixture();

        Response result = fixture.delete("11", mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void should_return_not_found_on_delete_if_id_is_null() {
        EventPlanRestManager fixture = getFixture();

        Response result = fixture.delete(null, mockRequest);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getStatus());
    }

    @Test
    public void should_return_not_found_on_delete_if_id_is_empty() {
        EventPlanRestManager fixture = getFixture();

        Response result = fixture.delete(null, mockRequest);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getStatus());
    }

    @Test
    public void should_return_not_found_on_delete_if_entity_not_exists() {
        EventPlanRestManager fixture = getFixture();

        Response result = fixture.delete("9999", mockRequest);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getStatus());
    }

    @Test
    public void should_deletes_entity() throws SQLException {
        Plan plan = testHelper.createPlanWithCategoryAndComponent("Plan 1", "Description", 1, 0, "EventCategory 1", "Component 1");
        EventPlanRestManager fixture = getFixture();

        Response result = fixture.delete(Integer.toString(plan.getID()), mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void on_Get_Should_Return_All_Resources() throws SQLException {
        Plan firstPlan = testHelper.createPlanWithCategoryAndComponent("Plan 1", "Description", 1, 0, "EventCategory 1", "Component 1");
        Plan secondPlan = testHelper.createPlanWithCategoryAndComponent("Plan 2", "Descriptio 2", 1, 0, "EventCategory 1234", "Component 124121");

        EventPlanRestManager fixture = getFixture();

        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals(2, doGetTransactionResult.length);
        assertEquals(firstPlan.getName(), ((PlanTemplate) doGetTransactionResult[0]).getName());
        assertEquals(secondPlan.getName(), ((PlanTemplate) doGetTransactionResult[1]).getName());
    }

    @Test
    public void on_Delete_with_unknown_id_should_response_not_found() throws SQLException {
        EventPlanRestManager fixture = getFixture();

        Response response = fixture.delete("1251252", mockRequest);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    public void on_Delete_should_remove_entity_with_specified_id() throws SQLException {
        Plan plan = testHelper.createPlanWithCategoryAndComponent("Plan 1", "Description", 1, 0, "EventCategory 1", "Component 1");
        EventPlanRestManager fixture = getFixture();

        Response response = fixture.delete(Integer.toString(plan.getID()), mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(0, activeObjects.count(Plan.class));
    }
}
