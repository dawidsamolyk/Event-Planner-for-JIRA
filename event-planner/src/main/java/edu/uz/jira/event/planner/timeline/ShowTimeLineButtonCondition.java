package edu.uz.jira.event.planner.timeline;

import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.util.ProjectUtils;
import edu.uz.jira.event.planner.util.text.Internationalization;

import javax.annotation.Nonnull;
import java.util.Map;

public class ShowTimeLineButtonCondition implements Condition {
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
        Object project = context.get("project");

        if (project instanceof Project) {
            Project projectObject = (Project) project;
            ProjectCategory projectCategory = projectObject.getProjectCategoryObject();

            boolean validProjectCategory = projectCategory.getName().equals(eventOrganizationProjectCategoryName);

            try {
                return validProjectCategory && projectUtils.getDueDateVersion(projectObject) != null;
            } catch (NullArgumentException e) {
                return false;
            }
        }
        return false;
    }
}
