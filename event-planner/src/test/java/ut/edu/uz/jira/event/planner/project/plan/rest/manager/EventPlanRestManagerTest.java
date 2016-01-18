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
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToCategoryRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.TaskToComponentRelation;
import edu.uz.jira.event.planner.database.xml.model.PlanTemplate;
import edu.uz.jira.event.planner.project.plan.rest.ActiveObjectWrapper;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventPlanRestManager;
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
public class EventPlanRestManagerTest {
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
                TransactionCallback<ActiveObjectWrapper[]> callback = (TransactionCallback) invocation.getArguments()[0];
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

        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.migrate(SubTaskToTaskRelation.class, TaskToComponentRelation.class, Category.class, Plan.class, Component.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToCategoryRelation.class);
        planService = new ActiveObjectsService(activeObjects);
        planService.clearDatabase();

        testHelper = new ActiveObjectsTestHelper(activeObjects);
    }

    private PlanTemplate getEmptyPlan() {
        return PlanTemplate.createEmpty();
    }

    @Test
    public void should_Get_Plan_From_Database() throws SQLException {
        String testPlanName = "Test name";
        String testPlanDescription = "Test description";
        String testDomainName = "Test domain";
        int testDays = 123;
        String testComponentName = "Test component";
        testHelper.createPlanWithDomainAndComponent(testPlanName, testPlanDescription, 0, testDays, testDomainName, testComponentName);
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        fixture.get(mockRequest);

        PlanTemplate expected = new PlanTemplate();
        expected.setName(testPlanName);
        expected.setDescription(testPlanDescription);
        expected.setCategoriesNames(new String[]{testDomainName});
        expected.setComponentsNames(new String[]{testComponentName});
        expected.setId(((PlanTemplate) transactionResult[0]).getId());
        assertEquals(expected, transactionResult[0]);
    }

    @Test
    public void should_Get_Many_Plans_From_Database() throws SQLException {
        testHelper.createPlanWithDomainAndComponent("Plan 1", "Description", 1, 0, "EventCategory 1", "Component 1");
        testHelper.createPlanWithDomainAndComponent("Plan 2", "Description", 1, 0, "EventCategory 2", "Component 1");
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        fixture.get(mockRequest);

        assertTrue(transactionResult.length == 2);
    }

    @Test
    public void should_Get_Empty_Plans_Array_When_There_Is_No_Plans_In_Database() {
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        fixture.get(mockRequest);

        assertTrue(transactionResult.length == 0);
    }

    @Test
    public void should_Put_New_Event_Plan() {
        Category category = testHelper.createDomainNamed("Test category");
        Component firstComponent = testHelper.createComponent("Test component 1", "");
        Component secondComponent = testHelper.createComponent("Test component 2", "");
        Component thirdComponent = testHelper.createComponent("Test component 3", "");
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplateForPut, planService);
        PlanTemplate config = new PlanTemplate();
        config.setName("Test name");
        config.setCategoriesNames(new String[]{category.getName()});
        config.setComponentsNames(new String[]{firstComponent.getName(), secondComponent.getName(), thirdComponent.getName()});

        Response result = fixture.post(config, mockRequest);

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), result.getStatus());
    }

    @Test
    public void on_Delete_should_remove_entity_with_specified_id() throws SQLException {
        Plan plan = testHelper.createPlanNamed("test name");
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response response = fixture.delete(Integer.toString(plan.getID()), mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(0, activeObjects.count(Plan.class));
    }

    @Test
    public void on_Post_should_get_entity_with_specified_id() throws SQLException {
        Plan plan = testHelper.createPlanNamed("test name");
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        Response response = fixture.post(Integer.toString(plan.getID()), mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        ActiveObjectWrapper expected = PlanTemplate.createEmpty().fill(plan);
        assertEquals(expected, transactionResult[0]);
    }
}
