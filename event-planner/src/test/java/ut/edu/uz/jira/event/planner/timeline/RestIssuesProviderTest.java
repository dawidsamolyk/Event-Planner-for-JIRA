package ut.edu.uz.jira.event.planner.timeline;

import com.atlassian.jira.avatar.Avatar;
import com.atlassian.jira.avatar.AvatarManager;
import com.atlassian.jira.avatar.AvatarService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.mock.MockAvatar;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.security.MockAuthenticationContext;
import com.atlassian.jira.mock.servlet.MockHttpServletRequest;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.user.util.MockUserManager;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import edu.uz.jira.event.planner.timeline.RestIssuesProvider;
import edu.uz.jira.event.planner.util.IssueDecorator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.ofbiz.core.entity.GenericEntityException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RestIssuesProviderTest {
    private UserManager mockUserManager;
    private TransactionTemplate mockTransactionTemplate;
    private IssueDecorator[] transactionResult;
    private MockProjectManager mockProjectManager;
    private IssueManager mockIssueManager;
    private Collection<Long> mockIssuesIds = new ArrayList<Long>();
    private List<Issue> mockIssues = new ArrayList<Issue>();
    private long defaultUserAvatarId = 1;
    private long mockAvatarId = 1002;

    @Before
    public void setUp() throws GenericEntityException {
        mockUserManager = mock(UserManager.class);
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(true);
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(mock(UserProfile.class));

        mockTransactionTemplate = mock(TransactionTemplate.class);

        Mockito.when(mockTransactionTemplate.execute(Mockito.any(TransactionCallback.class))).thenAnswer(new Answer<IssueDecorator[]>() {
            @Override
            public IssueDecorator[] answer(InvocationOnMock invocation) throws Throwable {
                TransactionCallback<IssueDecorator[]> callback = (TransactionCallback) invocation.getArguments()[0];
                transactionResult = callback.doInTransaction();
                return transactionResult;
            }
        });

        mockProjectManager = new MockProjectManager();
        mockIssueManager = mock(IssueManager.class);
        Mockito.when(mockIssueManager.getIssueIdsForProject(Mockito.anyLong())).thenReturn(mockIssuesIds);
        Mockito.when(mockIssueManager.getIssueObjects(Mockito.any(Collection.class))).thenReturn(mockIssues);

        MockUserManager mockUserManager = new MockUserManager();
        mockUserManager.addUser(new MockApplicationUser("test"));

        AvatarService mockAvatarService = Mockito.mock(AvatarService.class);
        Mockito.when(mockAvatarService.getAvatar(Mockito.any(ApplicationUser.class), Mockito.any(ApplicationUser.class))).thenReturn(new MockAvatar(mockAvatarId, "avatar.png", "image", Avatar.Type.USER, "test", true));

        AvatarManager mockAvatarManager = mock(AvatarManager.class);
        Mockito.when(mockAvatarManager.getDefaultAvatarId(Avatar.Type.USER)).thenReturn(defaultUserAvatarId);

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, Mockito.mock(ComponentAccessor.class))
                .addMock(com.atlassian.jira.user.util.UserManager.class, mockUserManager)
                .addMock(AvatarService.class, mockAvatarService)
                .addMock(AvatarManager.class, mockAvatarManager)
                .addMock(IssueManager.class, mockIssueManager)
                .addMock(ProjectManager.class, mockProjectManager)
                .addMock(JiraAuthenticationContext.class, new MockAuthenticationContext(new MockApplicationUser("test adminF")))
                .init();
    }

    @Test
    public void on_Get_Should_Response_Unauthorized_When_User_Is_Null() {
        Mockito.when(mockUserManager.getRemoteUser(Mockito.any(HttpServletRequest.class))).thenReturn(null);
        RestIssuesProvider fixture = new RestIssuesProvider(mockUserManager, mockTransactionTemplate);

        Response result = fixture.get(new MockHttpServletRequest());

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void on_Get_should_Response_Unauthorized_When_User_Is_Not_Admin() {
        Mockito.when(mockUserManager.isSystemAdmin(Mockito.any(UserKey.class))).thenReturn(false);
        RestIssuesProvider fixture = new RestIssuesProvider(mockUserManager, mockTransactionTemplate);

        Response result = fixture.get(new MockHttpServletRequest());

        assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), result.getStatus());
    }

    @Test
    public void if_project_key_was_not_specified_should_response_not_acceptable() {
        RestIssuesProvider fixture = new RestIssuesProvider(mockUserManager, mockTransactionTemplate);

        Response result = fixture.get(new MockHttpServletRequest());

        assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), result.getStatus());
    }

    @Test
    public void if_project_not_found_should_response_not_found() {
        RestIssuesProvider fixture = new RestIssuesProvider(mockUserManager, mockTransactionTemplate);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter(RestIssuesProvider.PROJECT_KEY_REQUEST_PARAMETER, "TEST");

        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getStatus());
    }

    @Test
    public void if_project_has_not_any_issue_then_should_response_not_found() {
        RestIssuesProvider fixture = new RestIssuesProvider(mockUserManager, mockTransactionTemplate);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        String testProjectKey = "TEST";
        mockRequest.setParameter(RestIssuesProvider.PROJECT_KEY_REQUEST_PARAMETER, testProjectKey);
        Project mockProject = mock(Project.class);
        Mockito.when(mockProject.getId()).thenReturn(11233l);
        Mockito.when(mockProject.getKey()).thenReturn(testProjectKey);
        mockProjectManager.addProject(mockProject);

        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getStatus());
    }

    @Test
    public void should_provide_issue_for_specified_Project() {
        RestIssuesProvider fixture = new RestIssuesProvider(mockUserManager, mockTransactionTemplate);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        String testProjectKey = "TEST";
        mockRequest.setParameter(RestIssuesProvider.PROJECT_KEY_REQUEST_PARAMETER, testProjectKey);
        Project mockProject = mock(Project.class);
        Mockito.when(mockProject.getId()).thenReturn(11233l);
        Mockito.when(mockProject.getKey()).thenReturn(testProjectKey);
        mockProjectManager.addProject(mockProject);
        Long mockIssueId = 123123l;
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getId()).thenReturn(mockIssueId);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(IssueDecoratorTest.getMockStatus());
        mockIssuesIds.add(mockIssueId);
        mockIssues.add(mockIssue);


        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals(mockIssueId, transactionResult[0].getId());
    }

    @Test
    public void should_provide_all_issues_for_specified_Project() {
        RestIssuesProvider fixture = new RestIssuesProvider(mockUserManager, mockTransactionTemplate);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        String testProjectKey = "TEST";
        mockRequest.setParameter(RestIssuesProvider.PROJECT_KEY_REQUEST_PARAMETER, testProjectKey);
        Project mockProject = mock(Project.class);
        Mockito.when(mockProject.getId()).thenReturn(11233l);
        Mockito.when(mockProject.getKey()).thenReturn(testProjectKey);
        mockProjectManager.addProject(mockProject);

        for (Long eachMockIssueId : new Long[]{123l, 5123l, 512l}) {
            Issue mockIssue = mock(Issue.class);
            Mockito.when(mockIssue.getId()).thenReturn(eachMockIssueId);
            Mockito.when(mockIssue.getStatusObject()).thenReturn(IssueDecoratorTest.getMockStatus());
            mockIssuesIds.add(eachMockIssueId);
            mockIssues.add(mockIssue);
        }

        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals(3, transactionResult.length);
    }

}
