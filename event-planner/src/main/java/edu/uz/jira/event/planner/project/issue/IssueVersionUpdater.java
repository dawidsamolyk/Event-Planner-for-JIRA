package edu.uz.jira.event.planner.project.issue;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.UpdateException;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.user.ApplicationUser;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import java.util.Collection;

public class IssueVersionUpdater {
    private final static IssueService ISSUE_SERVICE = ComponentAccessor.getIssueService();

    public void updateIssueVersion(@Nonnull final ApplicationUser user, @Nonnull final Long issueId, @Nonnull final Long versionId) throws UpdateException {
        IssueInputParameters versionValues = ISSUE_SERVICE.newIssueInputParameters();
        versionValues.setAffectedVersionIds(versionId);

        IssueService.UpdateValidationResult versionUpdateValidationResult = validateVersionUpdate(user, issueId, versionValues);

        IssueService.IssueResult versionResult = ISSUE_SERVICE.update(user, versionUpdateValidationResult);
        if (!versionResult.isValid()) {
            throw new UpdateException("Error while updating Issue Version");
        }
    }

    private IssueService.UpdateValidationResult validateVersionUpdate(@Nonnull ApplicationUser user, @Nonnull Long issueId, IssueInputParameters versionValues) throws UpdateException {
        IssueService.UpdateValidationResult versionUpdateValidationResult = ISSUE_SERVICE.validateUpdate(user, issueId, versionValues);

        if (!versionUpdateValidationResult.isValid()) {
            Collection<String> errorMessages = versionUpdateValidationResult.getErrorCollection().getErrorMessages();
            throw new UpdateException(StringUtils.join(errorMessages, " "));
        }

        return versionUpdateValidationResult;
    }
}
