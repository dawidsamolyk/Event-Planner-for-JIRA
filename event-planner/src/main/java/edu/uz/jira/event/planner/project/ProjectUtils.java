package edu.uz.jira.event.planner.project;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.util.text.Internationalization;

import javax.annotation.Nonnull;

/**
 * Helpers for working with Projects.
 */
public class ProjectUtils {
    private final I18nResolver internationalization;

    /**
     * Constructor.
     *
     * @param i18nResolver Injected {@code I18nResolver} implementation.
     */
    public ProjectUtils(@Nonnull final I18nResolver i18nResolver) {
        this.internationalization = i18nResolver;
    }

    /**
     * @return Version of the project which name is equal to Project Due Date.
     */
    public Version getDueDateVersion(@Nonnull final Project project) throws NullArgumentException {
        if (project == null) {
            throw new NullArgumentException(Project.class.getName());
        }
        Version result = null;
        String dueDateVersionName = internationalization.getText(Internationalization.PROJECT_VERSION_NAME);

        for (Version each : project.getVersions()) {
            if (each.getName().equals(dueDateVersionName)) {
                result = each;
            }
        }

        return result;
    }
}
