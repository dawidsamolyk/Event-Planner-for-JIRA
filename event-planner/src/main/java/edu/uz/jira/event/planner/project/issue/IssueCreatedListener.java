package edu.uz.jira.event.planner.project.issue;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.event.issue.IssueEvent;
import com.atlassian.jira.event.type.EventType;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.project.version.Version;
import com.atlassian.sal.api.message.I18nResolver;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class IssueCreatedListener implements InitializingBean, DisposableBean {
    private IssueService ISSUE_SERVICE = ComponentAccessor.getIssueService();
    private final EventPublisher eventPublisher;
    private final String projectVersionName;

    public IssueCreatedListener(EventPublisher eventPublisher, I18nResolver i18n) {
        this.projectVersionName = i18n.getText("project.version.name");
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        eventPublisher.register(this);
    }

    @Override
    public void destroy() throws Exception {
        eventPublisher.unregister(this);
    }

    @EventListener
    public void onIssueEvent(IssueEvent issueEvent) throws Exception {
        if (issueEvent.getEventTypeId().equals(EventType.ISSUE_CREATED_ID)) {
            Issue issue = issueEvent.getIssue();
            Version version = getEventDueDateVersion(issue);
            if (version != null) {
                setEventDueDateVersion(issue.getId(), issueEvent.getUser(), version.getId());
            }
        }
    }

    private Version getEventDueDateVersion(Issue issue) {
        for (Version eachVersion : issue.getProjectObject().getVersions()) {
            if (eachVersion.getName().equals(projectVersionName)) {
                return eachVersion;
            }
        }
        return null;
    }

    private void setEventDueDateVersion(Long issueId, User user, Long versionId) throws Exception {
        IssueInputParameters versionValues = ISSUE_SERVICE.newIssueInputParameters();
        versionValues.setAffectedVersionIds(versionId);

        IssueService.UpdateValidationResult validateVersionUpdate = ISSUE_SERVICE.validateUpdate(user, issueId, versionValues);

        if (!validateVersionUpdate.isValid()) {
            throw new Exception();
        }

        IssueService.IssueResult versionResult = ISSUE_SERVICE.update(user, validateVersionUpdate);
        if (!versionResult.isValid()) {
            throw new Exception();
        }
    }
}


