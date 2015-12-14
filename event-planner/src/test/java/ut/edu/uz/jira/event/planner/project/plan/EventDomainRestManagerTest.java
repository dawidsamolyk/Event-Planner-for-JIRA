package ut.edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import edu.uz.jira.event.planner.project.plan.EventOrganizationPlanService;
import edu.uz.jira.event.planner.project.plan.model.Domain;
import edu.uz.jira.event.planner.project.plan.rest.EventDomainRestManager;
import edu.uz.jira.event.planner.project.plan.rest.EventPlanRestManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class EventDomainRestManagerTest {
    private UserManager mockUserManager;
    private TransactionTemplate mockTransactionTemplate;
    private EventOrganizationPlanService planService;
    private ActiveObjects mockActiveObjects;
    private Object transactionResult;

    public static Domain getMockDomain(int id, String name, String description) {
        Domain result = mock(Domain.class);
        Mockito.when(result.getID()).thenReturn(id);
        Mockito.when(result.getName()).thenReturn(name);
        Mockito.when(result.getDescription()).thenReturn(description);
        return result;
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

        mockActiveObjects = mock(ActiveObjects.class);
        Mockito.when(mockActiveObjects.create(Domain.class)).thenAnswer(new Answer<Domain>() {
            @Override
            public Domain answer(InvocationOnMock invocation) throws Throwable {
                final Domain resultMock = mock(Domain.class);

                Mockito.doAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        Mockito.when(resultMock.getName()).thenReturn((String) invocation.getArguments()[0]);
                        return null;
                    }
                }).when(resultMock).setName(Mockito.anyString());

                Mockito.doAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        Mockito.when(resultMock.getDescription()).thenReturn((String) invocation.getArguments()[0]);
                        return null;
                    }
                }).when(resultMock).setDescription(Mockito.anyString());

                Mockito.doNothing().when(resultMock).save();

                return resultMock;
            }
        });

        planService = new EventOrganizationPlanService(mockActiveObjects);
    }

    @Test
    public void onGetShouldResponseUnauthorizedWhenUserIsNull() {
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(null);
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(new MockHttpServletRequest());

        assertEquals(Response.status(Response.Status.UNAUTHORIZED).build().getStatus(), result.getStatus());
    }

    @Test
    public void onPutShouldResponseUnauthorizedWhenUserIsNull() {
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(null);
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(getEmptyDomain(), new MockHttpServletRequest());

        assertEquals(Response.status(Response.Status.UNAUTHORIZED).build().getStatus(), result.getStatus());
    }

    private EventDomainRestManager.EventDomainConfig getEmptyDomain() {
        return EventDomainRestManager.EventDomainConfig.createEmpty();
    }

    @Test
    public void onGetshouldResponseUnauthorizedWhenUserIsNotAdmin() {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(new MockHttpServletRequest());

        assertEquals(Response.status(Response.Status.UNAUTHORIZED).build().getStatus(), result.getStatus());
    }

    @Test
    public void onPutshouldResponseUnauthorizedWhenUserIsNotAdmin() {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(getEmptyDomain(), new MockHttpServletRequest());

        assertEquals(Response.status(Response.Status.UNAUTHORIZED).build().getStatus(), result.getStatus());
    }

    @Test
    public void onPutshouldResponseNoContenWhenResourceIsNullOrEmpty() {
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(null, new MockHttpServletRequest());

        assertEquals(Response.status(Response.Status.NO_CONTENT).build().getStatus(), result.getStatus());
    }

    @Test
    public void shouldGetDomainFromDatabaseById() {
        String testName = "Test name";
        String testDescription = "Test description";
        Domain mockDomain = getMockDomain(2, testName, testDescription);
        Mockito.when(mockActiveObjects.find(Domain.class)).thenReturn(new Domain[]{mockDomain});
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("id", "1");
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        EventDomainRestManager.EventDomainConfig expected = new EventDomainRestManager.EventDomainConfig();
        expected.setName(testName);
        expected.setDescription(testDescription);
        assertEquals(expected, transactionResult);
    }

    @Test
    public void shouldGetEmptyPlanWhenIdWasNotSpecified() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        assertEquals(getEmptyDomain(), transactionResult);
    }

    @Test
    public void shouldGetEmptyPlanWhenIdIsNull() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("id", null);
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        assertEquals(getEmptyDomain(), transactionResult);
    }

    @Test
    public void shouldGetEmptyPlanWhenIdWasNotFound() {
        String testName = "Test name";
        String testDescription = "Test description";
        Domain mockDomain = getMockDomain(2, testName, testDescription);
        Mockito.when(mockActiveObjects.find(Domain.class)).thenReturn(new Domain[]{mockDomain});
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("id", "99999");
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        assertEquals(getEmptyDomain(), transactionResult);
    }

    @Test
    public void shouldGetEmptyPlanWhenIdIsEmpty() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("id", "");
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.get(mockRequest);

        assertEquals(getEmptyDomain(), transactionResult);
    }

    @Test
    public void shouldPutNewEventPlan() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);
        EventDomainRestManager.EventDomainConfig config = new EventDomainRestManager.EventDomainConfig();
        config.setName("Test name");
        config.setDescription("Test description");

        Response result = fixture.put(config, mockRequest);

        assertEquals(Response.status(Response.Status.OK).build().getStatus(), result.getStatus());
    }

    @Test
    public void shouldNotPutEmptyEventPlan() {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        Response result = fixture.put(new EventDomainRestManager.EventDomainConfig(), mockRequest);

        assertEquals(Response.status(Response.Status.NOT_ACCEPTABLE).build().getStatus(), result.getStatus());
    }

    @Test
    public void onPutShouldReturnInternalServerErrorWhenAnyExceptionOccursWhileAddingNewPlanToDatabase() throws SQLException {
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        EventDomainRestManager fixture = new EventDomainRestManager(mockUserManager, mockTransactionTemplate, planService);

        EventPlanRestManager.EventPlanConfig invalidConfig = new EventPlanRestManager.EventPlanConfig();
        invalidConfig.setName("Test name");
        invalidConfig.setDescription("Test description");
        invalidConfig.setDomains("Test domains");

        Response result = fixture.put(invalidConfig, mockRequest);

        assertEquals(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build().getStatus(), result.getStatus());
    }
}
