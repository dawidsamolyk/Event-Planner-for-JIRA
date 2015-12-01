package edu.uz.jira.event.planner.project.issue;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.UpdateException;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.user.ApplicationUser;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Updates version of the Issue.
 */
public class IssueVersionUpdater {
    private final static IssueService ISSUE_SERVICE = ComponentAccessor.getIssueService();

    /**
     * @param user      JIRA User.
     * @param issueId   ID of the Issue which should be updated.
     * @param versionId ID of the Version which should be applied to Issue.
     * @throws UpdateException Thrown when Issue Version update failed.
     */
    public void updateIssueVersion(@Nonnull final ApplicationUser user, @Nonnull final Long issueId, @Nonnull final Long versionId) throws UpdateException {
        IssueInputParameters versionValues = ISSUE_SERVICE.newIssueInputParameters();
        versionValues.setAffectedVersionIds(versionId);

        IssueService.UpdateValidationResult versionUpdateValidationResult = validateVersionUpdate(user, issueId, versionValues);

        IssueService.IssueResult versionResult = ISSUE_SERVICE.update(user, versionUpdateValidationResult);
        if (!versionResult.isValid()) {
            throw new UpdateException("Error while updating Issue Version");
        }
    }

    /**
     * @param user          JIRA User.
     * @param issueId       ID of the Issue which should be updated.
     * @param versionValues ID of the Version which should be applied to Issue.
     * @return Result of the Issue Version update validation.
     * @throws UpdateException Thrown when Issue Version update validation failed.
     */
    private IssueService.UpdateValidationResult validateVersionUpdate(@Nonnull ApplicationUser user, @Nonnull Long issueId, IssueInputParameters versionValues) throws UpdateException {
        IssueService.UpdateValidationResult versionUpdateValidationResult = ISSUE_SERVICE.validateUpdate(user, issueId, versionValues);

        if (!versionUpdateValidationResult.isValid()) {
            Collection<String> errorMessages = versionUpdateValidationResult.getErrorCollection().getErrorMessages();
            throw new UpdateException(StringUtils.join(errorMessages, " "));
        }

        return versionUpdateValidationResult;
    }
}
