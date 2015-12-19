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

        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.migrate(Domain.class, Plan.class, Component.class, Plan.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToDomainRelation.class);
        planService = new ActiveObjectsService(activeObjects);
        planService.clearDatabase();

        testHelper = new ActiveObjectsTestHelper(activeObjects);
    }

    private EventPlanRestManager.Configuration getEmptyPlan() {
        return EventPlanRestManager.Configuration.createEmpty();
    }

    @Test
    public void should_Get_Plan_From_Database() throws SQLException {
        String testPlanName = "Test name";
        String testPlanDescription = "Test description";
        String testDomainName = "Test domain";
        String testTime = "Test time";
        String testComponentName = "Test component";
        testHelper.createPlanWithDomainAndComponent(testPlanName, testPlanDescription, testTime, testDomainName, testComponentName);
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplateForGet, planService);

        fixture.get(mockRequest);

        EventPlanRestManager.Configuration expected = new EventPlanRestManager.Configuration();
        expected.setName(testPlanName);
        expected.setDescription(testPlanDescription);
        expected.setDomains(new String[]{testDomainName});
        expected.setComponents(new String[]{testComponentName});
        expected.setTime(testTime);
        expected.setId(((EventPlanRestManager.Configuration)transactionResult[0]).getId());
        assertEquals(expected, transactionResult[0]);
    }

    @Test
    public void should_Get_Many_Plans_From_Database() throws SQLException {
        testHelper.createPlanWithDomainAndComponent("Plan 1", "Description", "Test time", "Domain 1", "Component 1");
        testHelper.createPlanWithDomainAndComponent("Plan 2", "Description", "Test time", "Domain 2", "Component 1");
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
        Domain domain = testHelper.createDomainNamed("Test domain");
        Component firstComponent = testHelper.createComponent("Test component 1", "");
        Component secondComponent = testHelper.createComponent("Test component 2", "");
        Component thirdComponent = testHelper.createComponent("Test component 3", "");
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplateForPut, planService);
        EventPlanRestManager.Configuration config = new EventPlanRestManager.Configuration();
        config.setName("Test name");
        config.setTime("Test time");
        config.setDomains(new String[]{domain.getName()});
        config.setComponents(new String[]{firstComponent.getName(), secondComponent.getName(), thirdComponent.getName()});

        Response result = fixture.post(config, mockRequest);

        assertEquals(Response.Status.ACCEPTED.getStatusCode(), result.getStatus());
    }

}
