package ut.edu.uz.jira.event.planner.project.issue;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.event.MockEventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.user.MockUser;
import edu.uz.jira.event.planner.project.issue.IssueCreatedListener;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class IssueCreatedListenerTest {

    @Before
    public void setUp() {
        new MockComponentWorker()
                .addMock(EventPublisher.class, new MockEventPublisher())
                .addMock(IssueService.class, mock(IssueService.class))
                .init();
    }

    @Test
    public void listenerShouldDoNothingWhenIssueWasUpdated() throws Exception {
        IssueCreatedListener fixture = getFixture();
        Issue mockIssue = new MockIssue();
        IssueEvent mockIssueEvent = getMockIssueEvent(mockIssue, EventType.ISSUE_UPDATED_ID);

        fixture.onIssueEvent(mockIssueEvent);

        assertTrue(mockIssue.getFixVersions().isEmpty());
    }

    @Test
    public void listenerShouldAddNewVersionToIssueWhenItWasCreated() throws Exception {
        IssueCreatedListener fixture = getFixture();
        MockProject mockProject = new MockProject();
        addMockVersionToProject(mockProject);
        MockIssue mockIssue = new MockIssue();
        mockIssue.setProjectObject(mockProject);
        IssueEvent mockIssueEvent = getMockIssueEvent(mockIssue, EventType.ISSUE_CREATED_ID);

        fixture.onIssueEvent(mockIssueEvent);

        assertEquals(1, mockIssue.getFixVersions().size());
    }

    private void addMockVersionToProject(MockProject mockProject) {
        Collection<Version> mockVersions = new ArrayList<Version>();
        Version mockVersion = mock(Version.class);
        Mockito.when(mockVersion.getName()).thenReturn(IssueCreatedListener.PROJECT_VERSION_NAME);
        mockVersions.add(mockVersion);
        mockProject.setVersions(mockVersions);
    }

    private IssueEvent getMockIssueEvent(Issue issue, Long eventTypeId) {
        return new IssueEvent(issue, new HashMap(), new MockUser("test"), eventTypeId);
    }

    private IssueCreatedListener getFixture() {
        return new IssueCreatedListener(new MockEventPublisher());
    }
}
