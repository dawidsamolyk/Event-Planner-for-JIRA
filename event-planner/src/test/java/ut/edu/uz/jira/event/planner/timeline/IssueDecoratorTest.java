package ut.edu.uz.jira.event.planner.timeline;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.avatar.Avatar;
import com.atlassian.jira.avatar.AvatarManager;
import com.atlassian.jira.avatar.AvatarService;
import com.atlassian.jira.bc.project.component.MockProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.status.MockStatus;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.issue.status.category.StatusCategory;
import com.atlassian.jira.issue.status.category.StatusCategoryImpl;
import com.atlassian.jira.mock.MockAvatar;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.security.MockAuthenticationContext;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.user.MockUser;
import com.atlassian.jira.user.util.MockUserManager;
import com.atlassian.jira.user.util.UserManager;
import edu.uz.jira.event.planner.timeline.IssueDecorator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class IssueDecoratorTest {
    private AvatarService mockAvatarService;
    private long mockAvatarId = 1002;
    private long defaultUserAvatarId = 1;

    @Before
    public void setUp() {
        MockUserManager mockUserManager = new MockUserManager();
        mockUserManager.addUser(new MockApplicationUser("test"));

        mockAvatarService = Mockito.mock(AvatarService.class);
        Mockito.when(mockAvatarService.getAvatar(Mockito.any(ApplicationUser.class), Mockito.any(ApplicationUser.class))).thenReturn(new MockAvatar(mockAvatarId, "avatar.png", "image", Avatar.Type.USER, "test", true));

        AvatarManager mockAvatarManager = mock(AvatarManager.class);
        Mockito.when(mockAvatarManager.getDefaultAvatarId(Avatar.Type.USER)).thenReturn(defaultUserAvatarId);

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, Mockito.mock(ComponentAccessor.class))
                .addMock(UserManager.class, mockUserManager)
                .addMock(AvatarService.class, mockAvatarService)
                .addMock(AvatarManager.class, mockAvatarManager)
                .addMock(JiraAuthenticationContext.class, new MockAuthenticationContext(new MockApplicationUser("test adminF")))
                .init();
    }

    public static Status getMockStatus() {
        return new MockStatus("ID 12", "Test status", StatusCategoryImpl.getDefault());
    }

    @Test
    public void should_provide_summary() {
        String testSummary = "test summary";
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getSummary()).thenReturn(testSummary);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        Mockito.when(mockIssue.getAssignee()).thenReturn(new MockUser("test"));

        String result = new IssueDecorator(mockIssue).getSummary();

        assertEquals(testSummary, result);
    }

    @Test
    public void should_provide_component_name() {
        String testComponentName = "test component";
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        Mockito.when(mockIssue.getAssignee()).thenReturn(new MockUser("test"));
        Collection<ProjectComponent> mockComponents = new ArrayList<ProjectComponent>();
        mockComponents.add(new MockProjectComponent(123l, testComponentName));
        Mockito.when(mockIssue.getComponentObjects()).thenReturn(mockComponents);


        String[] result = new IssueDecorator(mockIssue).getComponentsNames();

        assertEquals(testComponentName, result[0]);
    }

    @Test
    public void should_provide_many_components_names() {
        String[] testComponentsNames = {"test component 1", "test component 2", "test component 3"};
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        Mockito.when(mockIssue.getAssignee()).thenReturn(new MockUser("test"));
        Collection<ProjectComponent> mockComponents = new ArrayList<ProjectComponent>();
        for (int index = 0; index < testComponentsNames.length; index++) {
            mockComponents.add(new MockProjectComponent((long) index, testComponentsNames[index]));
        }
        Mockito.when(mockIssue.getComponentObjects()).thenReturn(mockComponents);

        String[] result = new IssueDecorator(mockIssue).getComponentsNames();

        assertArrayEquals(testComponentsNames, result);
    }

    @Test
    public void should_indicate_done_if_issue_is_complete() {
        StatusCategory mockStatusCategory = mock(StatusCategory.class);
        Mockito.when(mockStatusCategory.getKey()).thenReturn(StatusCategory.COMPLETE);
        Status mockStatus = mock(Status.class);
        Mockito.when(mockStatus.getStatusCategory()).thenReturn(mockStatusCategory);
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(mockStatus);
        Mockito.when(mockIssue.getAssignee()).thenReturn(new MockUser("test"));

        boolean result = new IssueDecorator(mockIssue).isDone();

        assertEquals(true, result);
    }

    @Test
    public void should_indicate_not_done_if_issue_is_not_complete() {
        StatusCategory mockStatusCategory = mock(StatusCategory.class);
        Mockito.when(mockStatusCategory.getKey()).thenReturn(StatusCategory.IN_PROGRESS);
        Status mockStatus = mock(Status.class);
        Mockito.when(mockStatus.getStatusCategory()).thenReturn(mockStatusCategory);
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(mockStatus);
        Mockito.when(mockIssue.getAssignee()).thenReturn(new MockUser("test"));

        boolean result = new IssueDecorator(mockIssue).isDone();

        assertEquals(false, result);
    }

    @Test
    public void should_provide_assignee_avatar_id() {
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        User mockUser = new MockUser("test");
        Mockito.when(mockIssue.getAssignee()).thenReturn(mockUser);

        long result = new IssueDecorator(mockIssue).getAvatarId();

        assertEquals(mockAvatarId, result);
    }

    @Test
    public void if_assignee_avatar_id_is_zero_then_should_return_default_avatar_id() {
        long mockAvatarId = 0;
        Mockito.when(mockAvatarService.getAvatar(Mockito.any(ApplicationUser.class), Mockito.any(ApplicationUser.class))).thenReturn(new MockAvatar(mockAvatarId, "avatar.png", "image", Avatar.Type.USER, "test", true));
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        User mockUser = new MockUser("test");
        Mockito.when(mockIssue.getAssignee()).thenReturn(mockUser);

        long result = new IssueDecorator(mockIssue).getAvatarId();

        assertEquals(defaultUserAvatarId, result);
    }

    @Test
    public void if_assignee_is_null_then_should_return_default_avatar_id() {
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        Mockito.when(mockIssue.getAssignee()).thenReturn(null);

        long result = new IssueDecorator(mockIssue).getAvatarId();

        assertEquals(defaultUserAvatarId, result);
    }

    @Test
    public void should_provide_zero_days_before_today_if_issue_due_date_is_today() {
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        Mockito.when(mockIssue.getAssignee()).thenReturn(new MockUser("test"));
        Mockito.when(mockIssue.getDueDate()).thenReturn(new Timestamp(new Date().getTime()));

        int result = new IssueDecorator(mockIssue).getDaysAwayFromDueDate();

        assertEquals(0 + 1, result);
    }

    @Test
    public void should_provide_1_day_before_today_if_issue_was_due_yesterday() {
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        Mockito.when(mockIssue.getAssignee()).thenReturn(new MockUser("test"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        Mockito.when(mockIssue.getDueDate()).thenReturn(new Timestamp(calendar.getTimeInMillis()));

        int result = new IssueDecorator(mockIssue).getDaysAwayFromDueDate();

        assertEquals(-1 + 1, result);
    }

    @Test
    public void should_provide_1_day_after_today_if_issue_is_due_tomorrow() {
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        Mockito.when(mockIssue.getAssignee()).thenReturn(new MockUser("test"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Mockito.when(mockIssue.getDueDate()).thenReturn(new Timestamp(calendar.getTimeInMillis()));

        int result = new IssueDecorator(mockIssue).getDaysAwayFromDueDate();

        assertEquals(1 + 1, result);
    }

    @Test
    public void should_indicate_that_issue_is_late_if_was_due_yesterday() {
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        Mockito.when(mockIssue.getAssignee()).thenReturn(new MockUser("test"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -2);
        Mockito.when(mockIssue.getDueDate()).thenReturn(new Timestamp(calendar.getTimeInMillis()));

        boolean result = new IssueDecorator(mockIssue).isLate();

        assertEquals(true, result);
    }

    @Test
    public void should_not_indicate_that_issue_is_late_when_due_date_is_tomorrow() {
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        Mockito.when(mockIssue.getAssignee()).thenReturn(new MockUser("test"));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 1);
        Mockito.when(mockIssue.getDueDate()).thenReturn(new Timestamp(calendar.getTimeInMillis()));

        boolean result = new IssueDecorator(mockIssue).isLate();

        assertEquals(false, result);
    }

    @Test
    public void should_not_indicate_that_issue_is_late_when_due_date_is_today() {
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        Mockito.when(mockIssue.getAssignee()).thenReturn(new MockUser("test"));
        Mockito.when(mockIssue.getDueDate()).thenReturn(new Timestamp(new Date().getTime()));

        boolean result = new IssueDecorator(mockIssue).isLate();

        assertEquals(false, result);
    }

    @Test
    public void should_provide_assignee_name() {
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        String assigneeName = "test";
        Mockito.when(mockIssue.getAssignee()).thenReturn(new MockUser(assigneeName));
        Mockito.when(mockIssue.getDueDate()).thenReturn(new Timestamp(new Date().getTime()));

        String result = new IssueDecorator(mockIssue).getAssigneeName();

        assertEquals(assigneeName, result);
    }

    @Test
    public void should_provide_due_date_as_miliseconds() {
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getStatusObject()).thenReturn(getMockStatus());
        Mockito.when(mockIssue.getAssignee()).thenReturn(new MockUser("test"));
        Long dueDateTime = new Date().getTime();
        Mockito.when(mockIssue.getDueDate()).thenReturn(new Timestamp(dueDateTime));

        Long result = new IssueDecorator(mockIssue).getDueDate();

        assertEquals(dueDateTime, result);
    }
}
