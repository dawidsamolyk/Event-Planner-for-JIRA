package edu.uz.jira.event.planner.project.issue;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.exception.UpdateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.ApplicationUsers;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Nonnull;

/**
 * Listener for Issue creation. Setting Event Date Version to all created issues.
 */
public class IssueCreatedListener implements InitializingBean, DisposableBean {
    public static final String PROJECT_VERSION_NAME = "Event Due Date";
    private final EventPublisher EVENT_PUBLISHER;

    /**
     * @param eventPublisher Event publisher.
     */
    public IssueCreatedListener(@Nonnull final EventPublisher eventPublisher) {
        this.EVENT_PUBLISHER = eventPublisher;
    }

    /**
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        EVENT_PUBLISHER.register(this);
    }

    /**
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        EVENT_PUBLISHER.unregister(this);
    }

    /**
     * @param issueEvent Issue event.
     * @throws Exception
     */
    @EventListener
    public void onIssueEvent(@Nonnull final IssueEvent issueEvent) throws Exception {
        if (issueCreated(issueEvent)) {
            updateIssueFixVersion(issueEvent);
        }
    }

    /**
     * @param issueEvent Issue event.
     * @return Indicates that issue was created.
     */
    private boolean issueCreated(@Nonnull final IssueEvent issueEvent) {
        return issueEvent.getEventTypeId().equals(EventType.ISSUE_CREATED_ID);
    }

    /**
     * @param issueEvent Issue event.
     * @throws UpdateException Thrown when issue version updating failed.
     */
    private void updateIssueFixVersion(@Nonnull final IssueEvent issueEvent) throws UpdateException {
        Version version = getEventDueDateVersion(issueEvent.getProject());

        if (version != null) {
            ApplicationUser applicationUser = ApplicationUsers.from(issueEvent.getUser());
            Issue issue = issueEvent.getIssue();

            IssueFixVersionUpdater updater = new IssueFixVersionUpdater();
            updater.updateIssueFixVersion(applicationUser, issue.getId(), version.getId());
        }
    }

    /**
     * @param project Project.
     * @return Event due date Version.
     */
    private Version getEventDueDateVersion(@Nonnull final Project project) {
        for (Version eachVersion : project.getVersions()) {
            if (eachVersion.getName().equals(PROJECT_VERSION_NAME)) {
                return eachVersion;
            }
        }
        return null;
    }


}


