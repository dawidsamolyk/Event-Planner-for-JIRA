package edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.sal.api.message.I18nResolver;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.InvalidInputException;
import com.opensymphony.workflow.Validator;
import edu.uz.jira.event.planner.project.configuration.EventPlanConfigWebworkAction;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.util.Map;

public class IssueDueDateValidator implements Validator {
    private final I18nResolver i18nResolver;

    public IssueDueDateValidator(@Nonnull final I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
    }

    private Version getDueDateVersion(@Nonnull final Project project) {
        Version result = null;
        String dueDateVersionName = getInternationalized(EventPlanConfigWebworkAction.PROJECT_VERSION_NAME_KEY);

        for (Version each : project.getVersions()) {
            if (each.getName().equals(dueDateVersionName)) {
                return each;
            }
        }

        return result;
    }

    private String getInternationalized(String key) {
        return i18nResolver.getText(key);
    }

    @Override
    public void validate(Map transientVars, Map args, PropertySet ps) throws InvalidInputException {
        Issue issue = (Issue) transientVars.get("issue");
        Project project = issue.getProjectObject();

        Version projectDueDateVersion = getDueDateVersion(project);
        Timestamp issueDueDate = issue.getDueDate();

        if (issue.getDueDate() == null) {
            throw new InvalidInputException("Issue Due Date cannot be empty!");
        }
        if (projectDueDateVersion == null) {
            throw new InvalidInputException("Project hasn't Due Date Version!");
        }
        if (!issueDueDate.before(projectDueDateVersion.getReleaseDate())) {
            throw new InvalidInputException("Issue Due Date cannot be after Project Due Date!");
        }
    }
}
