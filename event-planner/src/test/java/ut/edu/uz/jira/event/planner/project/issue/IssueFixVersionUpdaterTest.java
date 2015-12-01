package ut.edu.uz.jira.event.planner.project.issue;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.bc.issue.DefaultIssueService;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.event.MockEventPublisher;
import com.atlassian.jira.issue.IssueFactory;
import com.atlassian.jira.issue.fields.MockFieldManager;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.issue.fields.layout.field.MockFieldLayoutManager;
import com.atlassian.jira.issue.fields.screen.FieldScreenRendererFactory;
import com.atlassian.jira.mock.MockIssueManager;
import com.atlassian.jira.mock.MockPermissionManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.web.action.issue.IssueCreationHelperBean;
import com.atlassian.jira.workflow.IssueWorkflowManager;
import com.atlassian.jira.workflow.WorkflowManager;
import edu.uz.jira.event.planner.project.issue.IssueFixVersionUpdater;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

public class IssueFixVersionUpdaterTest {

    @Before
    public void setUp() {
        new MockComponentWorker()
                .addMock(EventPublisher.class, new MockEventPublisher())
                .addMock(IssueService.class, new DefaultIssueService(
                        mock(IssueFactory.class),
                        mock(IssueCreationHelperBean.class),
                        new MockFieldManager(),
                        new MockIssueManager(),
                        new MockPermissionManager(),
                        mock(FieldScreenRendererFactory.class),
                        mock(WorkflowManager.class),
                        mock(IssueWorkflowManager.class),
                        new MockFieldLayoutManager(),
                        mock(FieldConfigSchemeManager.class),
                        new MockEventPublisher()
                ))
                .init();
    }

    @Test
    public void shouldUpdateIssueFixVersion() throws Exception {
        IssueFixVersionUpdater fixture = new IssueFixVersionUpdater();
        ApplicationUser mockUser = new MockApplicationUser("test");

        fixture.updateIssueFixVersion(mockUser, 3l, 3l);


    }
}
