package ut.edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import edu.uz.jira.event.planner.project.plan.EventPlanService;
import edu.uz.jira.event.planner.project.plan.model.Domain;
import edu.uz.jira.event.planner.project.plan.model.Plan;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventDomainRestManager;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventPlanRestManager;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ut.helpers.TestActiveObjects;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

@RunWith(ActiveObjectsJUnitRunner.class)

public class EventPlanRestManagerTest {
    private EntityManager entityManager;
    private UserManager mockUserManager;
    private TransactionTemplate mockTransactionTemplate;
    private EventPlanService planService;
    private ActiveObjects activeObjects;
    private Object transactionResult;

    public Plan createActiveObjects(String planName, String planDescription, String planTime, String domainName) {
        Domain domain = activeObjects.create(Domain.class);
        domain.setName(domainName);
        domain.save();

        Plan plan = activeObjects.create(Plan.class);
        plan.setName(planName);
        plan.setDescription(planDescription);
        plan.setTimeToComplete(planTime);
        plan.save();

        PlanToDomainRelation relation = activeObjects.create(PlanToDomainRelation.class);
        relation.setPlan(plan);
        relation.setDomain(domain);
        relation.save();

        return plan;
    }

    @Before
    public void setUp() {
        mockUserManager = mock(UserManager.class);
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(mock(UserProfile.class));
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(true);

        mockTransactionTemplate = mock(TransactionTemplate.class);
        Mockito.when(mockTransactionTemplate.execute(Mockito.any(TransactionCallback.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TransactionCallback callback = (TransactionCallback) invocation.getArguments()[0];
                transactionResult = callback.doInTransaction();
                return null;
            }
        });

        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.flushAll();
        activeObjects.migrate(Domain.class, Plan.class);
        planService = new EventPlanService(activeObjects);
    }

    @Test
    public void onGetShouldResponseUnauthorizedWhenUserIsNull() {
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(null);
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(new MockHttpServletRequest());

        assertEquals(Response.status(Response.Status.UNAUTHORIZED).build().getStatus(), result.getStatus());
    }

    @Test
    public void onPutShouldResponseUnauthorizedWhenUserIsNull() {
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(null);
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(getEmptyPlan(), new MockHttpServletRequest());

        assertEquals(Response.status(Response.Status.UNAUTHORIZED).build().getStatus(), result.getStatus());
    }

    private EventPlanRestManager.EventPlanConfig getEmptyPlan() {
        return EventPlanRestManager.EventPlanConfig.createEmpty();
    }

    @Test
    public void onGetshouldResponseUnauthorizedWhenUserIsNotAdmin() {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(new MockHttpServletRequest());

        assertEquals(Response.status(Response.Status.UNAUTHORIZED).build().getStatus(), result.getStatus());
    }

    @Test
    public void onPutshouldResponseUnauthorizedWhenUserIsNotAdmin() {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(getEmptyPlan(), new MockHttpServletRequest());

        assertEquals(Response.status(Response.Status.UNAUTHORIZED).build().getStatus(), result.getStatus());
    }

    @Test
    public void onPutshouldResponseNoContenWhenResourceIsNullOrEmpty() {
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(null, new MockHttpServletRequest());

        assertEquals(Response.status(Response.Status.NO_CONTENT).build().getStatus(), result.getStatus());
    }

    @Test
    public void shouldGetPlanFromDatabaseById() {
        String testPlanName = "Test name";
        String testPlanDescription = "Test description";
        String testDomainName = "Test domain";
        String testTime = "Test time";
        Plan mockPlan = createActiveObjects(testPlanName, testPlanDescription, testTime, testDomainName);

        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("id", Integer.toString(mockPlan.getID()));
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        EventPlanRestManager.EventPlanConfig expected = new EventPlanRestManager.EventPlanConfig();
        expected.setName(testPlanName);
        expected.setDescription(testPlanDescription);
        expected.setDomains(testDomainName);
        expected.setTime(testTime);
        assertEquals(expected, transactionResult);
    }

    @Test
    public void shouldGetEmptyPlanWhenIdWasNotSpecified() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        assertEquals(getEmptyPlan(), transactionResult);
    }

    @Test
    public void shouldGetEmptyPlanWhenIdIsNull() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("id", null);
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        assertEquals(getEmptyPlan(), transactionResult);
    }

    @Test
    public void shouldGetEmptyPlanWhenIdWasNotFound() {
        Plan mockPlan = createActiveObjects("Test name", "Test description", "Test time", "Test domain");
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("id", Integer.toString(mockPlan.getID() + 9123));
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        assertEquals(getEmptyPlan(), transactionResult);
    }

    @Test
    public void shouldGetEmptyPlanWhenIdIsEmpty() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("id", "");
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        assertEquals(getEmptyPlan(), transactionResult);
    }

    @Test
    public void shouldPutNewEventPlan() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);
        EventPlanRestManager.EventPlanConfig config = new EventPlanRestManager.EventPlanConfig();
        config.setName("Test name");
        config.setTime("Test time");
        config.setDomains("Test domain");

        Response result = fixture.put(config, mockRequest);

        assertEquals(Response.status(Response.Status.OK).build().getStatus(), result.getStatus());
    }

    @Test
    public void shouldNotPutEmptyEventPlan() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(new EventPlanRestManager.EventPlanConfig(), mockRequest);

        assertEquals(Response.status(Response.Status.NOT_ACCEPTABLE).build().getStatus(), result.getStatus());
    }

    @Test
    public void onPutShouldReturnInternalServerErrorWhenAnyExceptionOccursWhileAddingNewPlanToDatabase() throws SQLException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        EventDomainRestManager.EventDomainConfig invalidConfig = new EventDomainRestManager.EventDomainConfig();
        invalidConfig.setName("Test name");

        Response result = fixture.put(invalidConfig, mockRequest);

        assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build().getStatus(), result.getStatus());
    }
}
