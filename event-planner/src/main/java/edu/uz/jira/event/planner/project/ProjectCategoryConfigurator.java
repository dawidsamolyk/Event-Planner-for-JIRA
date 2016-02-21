package edu.uz.jira.event.planner.project;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.util.text.Internationalization;

import javax.annotation.Nonnull;

/**
 * Creates and assigns project category to specified project.F
 */
public class ProjectCategoryConfigurator {
    private final ProjectManager projectManager;
    private final I18nResolver internationalization;

    /**
     * Constructor.
     *
     * @param i18nResolver Injected {@code I18nResolver} implementation.
     */
    public ProjectCategoryConfigurator(@Nonnull final I18nResolver i18nResolver) {
        this.projectManager = ComponentAccessor.getProjectManager();
        this.internationalization = i18nResolver;
    }

    /**
     * @return Event Organization Project Category.
     */
    public ProjectCategory getProjectCategory() {
        String name = getInternalized(Internationalization.PROJECT_CATEGORY_NAME);
        String description = getInternalized(Internationalization.PROJECT_CATEGORY_DESCRIPTION);

        ProjectCategory result = projectManager.getProjectCategoryObjectByName(name);

        if (result == null) {
            result = projectManager.createProjectCategory(name, description);
        }
        return result;
    }

    private String getInternalized(String key) {
        return internationalization.getText(key);
    }

    /**
     * @param projectCategory Project Category to assign to Project.
     * @param project         Project.
     */
    public void assign(@Nonnull final ProjectCategory projectCategory, @Nonnull final Project project) {
        projectManager.setProjectCategory(project, projectCategory);
    }
}
