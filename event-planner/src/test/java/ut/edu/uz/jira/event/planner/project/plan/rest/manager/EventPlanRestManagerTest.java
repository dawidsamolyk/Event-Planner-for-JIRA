package ut.edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import edu.uz.jira.event.planner.project.plan.EventPlanService;
import edu.uz.jira.event.planner.project.plan.model.*;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.project.plan.rest.EventRestConfiguration;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventDomainRestManager;
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
    private TransactionTemplate mockTransactionTemplate;
    private EventPlanService planService;
    private ActiveObjects activeObjects;
    private Object transactionResult;

    public void createPlanWithDomainAndComponent(String planName, String planDescription, String planTime, String domainName, String componentName) throws SQLException {
        Domain domain = activeObjects.create(Domain.class);
        domain.setName(domainName);
        domain.save();

        Component component = activeObjects.create(Component.class);
        component.setName(componentName);
        component.save();

        Plan plan = activeObjects.create(Plan.class);
        plan.setName(planName);
        plan.setDescription(planDescription);
        plan.setTimeToComplete(planTime);
        plan.save();

        PlanToDomainRelation relation = activeObjects.create(PlanToDomainRelation.class);
        relation.setPlan(plan);
        relation.setDomain(domain);
        relation.save();

        PlanToComponentRelation relation2 = activeObjects.create(PlanToComponentRelation.class);
        relation2.setComponent(component);
        relation2.setPlan(plan);
        relation2.save();
    }

    @Before
    public void setUp() {
        mockRequest = new MockHttpServletRequest();

        mockUserManager = mock(UserManager.class);
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(mock(UserProfile.class));
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(true);

        mockTransactionTemplate = mock(TransactionTemplate.class);
        Mockito.when(mockTransactionTemplate.execute(Mockito.any(TransactionCallback.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                TransactionCallback callback = (TransactionCallback) invocation.getArguments()[0];
                transactionResult = callback.doInTransaction();
                return transactionResult;
            }
        });

        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.migrate(Domain.class, Plan.class, Component.class, Plan.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToDomainRelation.class);
        planService = new EventPlanService(activeObjects);
        planService.clearDatabase();
    }

    @Test
    public void onGetShouldResponseUnauthorizedWhenUserIsNull() {
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(null);
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void onPutShouldResponseUnauthorizedWhenUserIsNull() {
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(null);
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(getEmptyPlan(), mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    private EventPlanRestManager.Configuration getEmptyPlan() {
        return EventPlanRestManager.Configuration.createEmpty();
    }

    @Test
    public void onGetshouldResponseUnauthorizedWhenUserIsNotAdmin() {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void onPutshouldResponseUnauthorizedWhenUserIsNotAdmin() {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(getEmptyPlan(), mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void onPutshouldResponseNoContenWhenResourceIsNullOrEmpty() {
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(null, mockRequest);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), result.getStatus());
    }

    @Test
    public void shouldGetPlanFromDatabase() throws SQLException {
        String testPlanName = "Test name";
        String testPlanDescription = "Test description";
        String testDomainName = "Test domain";
        String testTime = "Test time";
        String testComponentName = "Test component";
        createPlanWithDomainAndComponent(testPlanName, testPlanDescription, testTime, testDomainName, testComponentName);
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        fixture.get(mockRequest);

        EventPlanRestManager.Configuration expected = new EventPlanRestManager.Configuration();
        expected.setName(testPlanName);
        expected.setDescription(testPlanDescription);
        expected.setDomains(new String[]{testDomainName});
        expected.setComponents(new String[]{testComponentName});
        expected.setTime(testTime);
        assertEquals(expected, ((EventRestConfiguration[]) transactionResult)[0]);
    }

    @Test
    public void shouldGetManyPlansFromDatabase() throws SQLException {
        createPlanWithDomainAndComponent("Plan 1", "Description", "Test time", "Domain 1", "Component 1");
        createPlanWithDomainAndComponent("Plan 2", "Description", "Test time", "Domain 2", "Component 1");
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        fixture.get(mockRequest);

        assertTrue(((EventRestConfiguration[]) transactionResult).length == 2);
    }

    @Test
    public void shouldGetEmptyPlansArrayWhenThereIsNoPlansInDatabase() {
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        fixture.get(mockRequest);

        assertTrue(((EventRestConfiguration[]) transactionResult).length == 0);
    }

    @Test
    public void shouldPutNewEventPlan() {
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);
        EventPlanRestManager.Configuration config = new EventPlanRestManager.Configuration();
        config.setName("Test name");
        config.setTime("Test time");
        config.setDomains(new String[]{"Test domains"});
        config.setComponents(new String[]{"Test component"});

        Response result = fixture.put(config, mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
    }

    @Test
    public void shouldNotPutEmptyEventPlan() {
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(new EventPlanRestManager.Configuration(), mockRequest);

        assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), result.getStatus());
    }

    @Test
    public void onPutShouldReturnInternalServerErrorWhenAnyExceptionOccursWhileAddingNewPlanToDatabase() throws SQLException {
        EventPlanRestManager fixture = new EventPlanRestManager(mockUserManager, mockTransactionTemplate, planService);
        EventDomainRestManager.Configuration invalidConfiguration = new EventDomainRestManager.Configuration();
        invalidConfiguration.setName("Test name");

        Response result = fixture.put(invalidConfiguration, mockRequest);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result.getStatus());
    }
}
