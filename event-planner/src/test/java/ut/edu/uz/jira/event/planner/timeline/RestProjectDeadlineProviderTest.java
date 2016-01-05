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
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.user.util.MockUserManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import edu.uz.jira.event.planner.timeline.RestProjectDeadlineProvider;
import edu.uz.jira.event.planner.util.text.Internationalization;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.ofbiz.core.entity.GenericEntityException;

import javax.ws.rs.core.Response;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class RestProjectDeadlineProviderTest {
    private TransactionTemplate mockTransactionTemplate;
    private long transactionResult;
    private MockProjectManager mockProjectManager;
    private IssueManager mockIssueManager;
    private I18nResolver mockInternationalization;
    private Collection<Long> mockIssuesIds = new ArrayList<Long>();
    private List<Issue> mockIssues = new ArrayList<Issue>();
    private long defaultUserAvatarId = 1;
    private long mockAvatarId = 1002;
    private static final String DUE_DATE_VERSION_NAME = "Event Due Date";;

    @Before
    public void setUp() throws GenericEntityException {
        mockTransactionTemplate = mock(TransactionTemplate.class);

        Mockito.when(mockTransactionTemplate.execute(Mockito.any(TransactionCallback.class))).thenAnswer(new Answer<Long>() {
            @Override
            public Long answer(InvocationOnMock invocation) throws Throwable {
                TransactionCallback<Long> callback = (TransactionCallback) invocation.getArguments()[0];
                transactionResult = callback.doInTransaction();
                return transactionResult;
            }
        });

        mockInternationalization = mock(I18nResolver.class);
        Mockito.when(mockInternationalization.getText(Internationalization.PROJECT_VERSION_NAME)).thenReturn(DUE_DATE_VERSION_NAME);

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
    public void if_project_key_was_not_specified_should_response_not_acceptable() {
        RestProjectDeadlineProvider fixture = new RestProjectDeadlineProvider(mockTransactionTemplate, mockInternationalization);

        Response result = fixture.get(new MockHttpServletRequest());

        assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), result.getStatus());
    }

    @Test
    public void if_project_not_found_should_response_not_found() {
        RestProjectDeadlineProvider fixture = new RestProjectDeadlineProvider(mockTransactionTemplate, mockInternationalization);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        mockRequest.setParameter(RestProjectDeadlineProvider.PROJECT_KEY_REQUEST_PARAMETER, "TEST");

        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getStatus());
    }

    @Test
    public void if_project_has_not_due_date_version_then_should_respond_not_found() {
        RestProjectDeadlineProvider fixture = new RestProjectDeadlineProvider(mockTransactionTemplate, mockInternationalization);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        String testProjectKey = "TEST";
        MockProject mockProject = new MockProject(123l, testProjectKey);
        Version mockVersion = mock(Version.class);
        Mockito.when(mockVersion.getName()).thenReturn("Test version");
        mockProject.setVersions(Arrays.asList(mockVersion));
        mockProjectManager.addProject(mockProject);
        mockRequest.setParameter(RestProjectDeadlineProvider.PROJECT_KEY_REQUEST_PARAMETER, testProjectKey);

        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getStatus());
    }

    @Test
    public void if_project_has_due_date_version_then_should_return_deadline_date() {
        RestProjectDeadlineProvider fixture = new RestProjectDeadlineProvider(mockTransactionTemplate, mockInternationalization);
        MockHttpServletRequest mockRequest = new MockHttpServletRequest();
        String testProjectKey = "TEST";
        Date testDeadline = new Date();
        MockProject mockProject = new MockProject(123l, testProjectKey);
        Version mockVersion = mock(Version.class);
        Mockito.when(mockVersion.getName()).thenReturn(DUE_DATE_VERSION_NAME);
        Mockito.when(mockVersion.getReleaseDate()).thenReturn(testDeadline);
        mockProject.setVersions(Arrays.asList(mockVersion));
        mockProjectManager.addProject(mockProject);
        mockRequest.setParameter(RestProjectDeadlineProvider.PROJECT_KEY_REQUEST_PARAMETER, testProjectKey);

        Response result = fixture.get(mockRequest);

        assertEquals(Response.Status.OK.getStatusCode(), result.getStatus());
        assertEquals(testDeadline.getTime(), transactionResult);
    }

}
