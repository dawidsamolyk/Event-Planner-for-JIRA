package edu.uz.jira.event.planner.timeline.condition;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.project.ProjectUtils;
import edu.uz.jira.event.planner.util.text.Internationalization;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import java.util.Map;

public class ShowTimeLineButtonCondition implements Condition {
    public static final String PROJECT_KEY = "project";
    private final ProjectUtils projectUtils;
    private String eventOrganizationProjectCategoryName;

    public ShowTimeLineButtonCondition(@Nonnull final I18nResolver i18nResolver) {
        eventOrganizationProjectCategoryName = i18nResolver.getText(Internationalization.PROJECT_CATEGORY_NAME);
        projectUtils = new ProjectUtils(i18nResolver);
    }

    @Override
    public void init(final Map<String, String> params) throws PluginParseException {
    }

    @Override
    public boolean shouldDisplay(final Map<String, Object> context) {
        Object project = context.get(PROJECT_KEY);

        if (project instanceof Project) {
            Project projectObject = (Project) project;

            return isValidProjectCategory(projectObject) && hasProjectDeadlineVersion(projectObject);
        }
        return false;
    }

    private boolean hasProjectDeadlineVersion(final Project project) {
        try {
            return projectUtils.getDueDateVersion(project) != null;
        } catch (NullArgumentException e) {
            return false;
        }
    }

    private boolean isValidProjectCategory(final Project project) {
        if (project == null) {
            return false;
        }
        ProjectCategory projectCategoryObject = project.getProjectCategoryObject();
        if (projectCategoryObject == null) {
            return false;
        }

        String projectCategoryName = projectCategoryObject.getName();
        if (StringUtils.isBlank(projectCategoryName)) {
            return false;
        }
        return projectCategoryName.equals(eventOrganizationProjectCategoryName);
    }
}
