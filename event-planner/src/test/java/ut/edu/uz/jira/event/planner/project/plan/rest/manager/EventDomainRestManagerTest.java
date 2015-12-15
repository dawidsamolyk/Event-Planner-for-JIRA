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
import edu.uz.jira.event.planner.project.plan.model.Domain;
import edu.uz.jira.event.planner.project.plan.model.Plan;
import edu.uz.jira.event.planner.project.plan.rest.EventRestConfiguration;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventDomainRestManager;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventPlanRestManager;
import net.java.ao.EntityManager;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.DerbyEmbedded;
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
@Jdbc(DerbyEmbedded.class)
@NameConverters
public class EventDomainRestManagerTest {
    private EntityManager entityManager;
    private MockHttpServletRequest mockRequest;
    private UserManager mockUserManager;
    private TransactionTemplate mockTransactionTemplate;
    private EventPlanService planService;
    private ActiveObjects activeObjects;
    private Object transactionResult;

    public Domain createDomain(String name, String description) throws SQLException {
        Domain domain = activeObjects.create(Domain.class);
        domain.setName(name);
        domain.setDescription(description);
        domain.save();
        return domain;
    }

    private EventDomainRestManager.EventDomainConfig getEmptyDomain() {
        return EventDomainRestManager.EventDomainConfig.createEmpty();
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

        activeObjects = mock(ActiveObjects.class);
        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.flushAll();
        activeObjects.migrate(Domain.class, Plan.class);
        planService = new EventPlanService(activeObjects);
    }

    @Test
    public void onGetShouldResponseUnauthorizedWhenUserIsNull() {
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(null);
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void onPutShouldResponseUnauthorizedWhenUserIsNull() {
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(null);
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(getEmptyDomain(), mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void onGetshouldResponseUnauthorizedWhenUserIsNotAdmin() {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void onPutshouldResponseUnauthorizedWhenUserIsNotAdmin() {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(getEmptyDomain(), mockRequest);

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void onPutshouldResponseNoContenWhenResourceIsNull() {
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(null, mockRequest);

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), result.getStatus());
    }

    @Test
    public void shouldGetDomainFromDatabase() throws SQLException {
        String testName = "Test name";
        String testDescription = "Test description";
        createDomain(testName, testDescription);
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        fixture.get(mockRequest);

        EventDomainRestManager.EventDomainConfig expected = new EventDomainRestManager.EventDomainConfig();
        expected.setName(testName);
        expected.setDescription(testDescription);
        assertEquals(expected, ((EventRestConfiguration[]) transactionResult)[0]);
    }

    @Test
    public void shouldGetManyDomainsFromDatabase() throws SQLException {
        createDomain("Domain 1", "Description");
        createDomain("Domain 2", "Description");
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        fixture.get(mockRequest);

        assertTrue(((EventRestConfiguration[]) transactionResult).length == 2);
    }

    @Test
    public void shouldGetEmptyDomainsArrayWhenThereIsNoDomainsInDatabase() {
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        fixture.get(mockRequest);

        assertTrue(((EventRestConfiguration[]) transactionResult).length == 0);
    }

    @Test
    public void shouldPutNewDomain() {
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);
        EventDomainRestManager.EventDomainConfig config = new EventDomainRestManager.EventDomainConfig();
        config.setName("Test name");
        config.setDescription("Test description");

        Response result = fixture.put(config, mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
    }

    @Test
    public void shouldNotPutEmptyDomain() {
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(getEmptyDomain(), mockRequest);

        assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), result.getStatus());
    }

    @Test
    public void onPutShouldReturnInternalServerErrorWhenAnyExceptionOccursWhileAddingNewDomainToDatabase() throws SQLException {
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);
        EventPlanRestManager.EventPlanConfig invalidConfig = new EventPlanRestManager.EventPlanConfig();
        invalidConfig.setName("Test name");
        invalidConfig.setDescription("Test description");
        invalidConfig.setDomains(new String[]{"Test domain"});
        invalidConfig.setTime("Test time");

        Response result = fixture.put(invalidConfig, mockRequest);

        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), result.getStatus());
    }
}
