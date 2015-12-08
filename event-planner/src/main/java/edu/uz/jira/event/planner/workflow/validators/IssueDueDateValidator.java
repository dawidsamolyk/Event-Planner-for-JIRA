package edu.uz.jira.event.planner.workflow.validators;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.sal.api.message.I18nResolver;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;
import edu.uz.jira.event.planner.exceptions.NullArgumentException;
import edu.uz.jira.event.planner.project.ProjectUtils;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

/**
 * Validator for JIRA Workflow. Validates Issue Due Date.
 */
public class IssueDueDateValidator implements Validator {
    private final ProjectUtils PROJECT_UTILS;

    /**
     * Constructor.
     *
     * @param i18nResolver Injected {@code I18nResolver} implementation.
     */
    public IssueDueDateValidator(@Nonnull final I18nResolver i18nResolver) {
        PROJECT_UTILS = new ProjectUtils(i18nResolver);
    }

    /**
     * Validates Issue Due Date.
     *
     * @param transientVars
     * @param args
     * @param ps
     * @throws InvalidInputException Thrown when issue's due date is empty or issue's project hasn't Due Date Version or project Due Date Version hasn't release date or issue Due Date is after project Due Date.
     */
    @Override
    public void validate(final Map transientVars, final Map args, final PropertySet ps) throws InvalidInputException {
        Issue issue = (Issue) transientVars.get("issue");

        try {
            validate(issue);
        } catch (NullArgumentException e) {
            throw new InvalidInputException(e.getMessage());
        }
    }

    /**
     * Validates Issue Due Date.
     *
     * @param issue Issue to validate.
     * @throws InvalidInputException Thrown when issue's due date is empty or issue's project hasn't Due Date Version or project Due Date Version hasn't release date or issue Due Date is after project Due Date.
     */
    public void validate(@Nonnull final Issue issue) throws InvalidInputException, NullArgumentException {
        if (issue == null) {
            return;
        }
        Project project = issue.getProjectObject();
        Version projectDueDateVersion = PROJECT_UTILS.getDueDateVersion(project);
        Date projectReleaseDate = projectDueDateVersion.getReleaseDate();

        validate(issue.getDueDate(), projectReleaseDate);
    }

    public void validate(final Timestamp issueDueDate, final Date projectReleaseDate) throws InvalidInputException {
        if (issueDueDate == null) {
            throw new InvalidInputException("Issue Due Date cannot be empty!");
        }
        if (projectReleaseDate != null && issueDueDate != null && issueDueDate.after(projectReleaseDate)) {
            throw new InvalidInputException("Issue Due Date cannot be after Project Due Date!");
        }
    }
}
