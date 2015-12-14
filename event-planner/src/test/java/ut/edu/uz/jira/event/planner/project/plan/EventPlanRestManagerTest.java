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
import edu.uz.jira.event.planner.project.plan.model.Plan;
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

public class EventPlanRestManagerTest {
    private UserManager mockUserManager;
    private TransactionTemplate mockTransactionTemplate;
    private EventOrganizationPlanService planService;
    private ActiveObjects mockActiveObjects;
    private Object transactionResult;

    public static Plan getMockPlanWithOneDomain(int id, String planName, String planDescription, String planTime, String domainName) {
        Plan result = mock(Plan.class);
        Mockito.when(result.getID()).thenReturn(id);
        Mockito.when(result.getName()).thenReturn(planName);
        Mockito.when(result.getDescription()).thenReturn(planDescription);
        Mockito.when(result.getTimeToComplete()).thenReturn(planTime);
        Domain mockDomain = EventDomainRestManagerTest.getMockDomain(2, domainName, "");
        Mockito.when(result.getRelatedDomains()).thenReturn(new Domain[]{mockDomain});
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
        Mockito.when(mockActiveObjects.create(Plan.class)).thenAnswer(new Answer<Plan>() {
            @Override
            public Plan answer(InvocationOnMock invocation) throws Throwable {
                final Plan resultMock = mock(Plan.class);

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

                Mockito.doAnswer(new Answer() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        Mockito.when(resultMock.getTimeToComplete()).thenReturn((String) invocation.getArguments()[0]);
                        return null;
                    }
                }).when(resultMock).setTimeToComplete(Mockito.anyString());

                Mockito.doNothing().when(resultMock).save();

                return resultMock;
            }
        });

        planService = new EventOrganizationPlanService(mockActiveObjects);
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
        Plan mockPlan = getMockPlanWithOneDomain(1, testPlanName, testPlanDescription, testTime, testDomainName);
        Mockito.when(mockActiveObjects.find(Plan.class)).thenReturn(new Plan[]{mockPlan});
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("id", "1");
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
        Plan mockPlan = getMockPlanWithOneDomain(1, "Test name", "Test description", "Test time", "Test domain");
        Mockito.when(mockActiveObjects.find(Plan.class)).thenReturn(new Plan[]{mockPlan});
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter("id", "99999");
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
