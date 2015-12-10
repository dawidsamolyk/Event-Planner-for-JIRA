package edu.uz.jira.event.planner.project;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.util.Internationalization;

import javax.annotation.Nonnull;

/**
 * Creates and assigns project category to specified project.F
 */
public class ProjectCategoryConfigurator {
    private final ProjectManager PROJECT_MANAGER;
    private final I18nResolver INTERNATIONALIZATION;

    /**
     * Constructor.
     *
     * @param i18nResolver Injected {@code I18nResolver} implementation.
     */
    public ProjectCategoryConfigurator(@Nonnull final I18nResolver i18nResolver) {
        this.PROJECT_MANAGER = ComponentAccessor.getProjectManager();
        this.INTERNATIONALIZATION = i18nResolver;
    }

    /**
     * @return Project Category.
     */
    public ProjectCategory createProjectCategory() {
        String name = getInternalized(Internationalization.PROJECT_CATEGORY_NAME);
        String description = getInternalized(Internationalization.PROJECT_CATEGORY_DESCRIPTION);

        ProjectCategory result = PROJECT_MANAGER.getProjectCategoryObjectByName(name);

        if (result == null) {
            result = PROJECT_MANAGER.createProjectCategory(name, description);
        }
        return result;
    }

    private String getInternalized(String key) {
        return INTERNATIONALIZATION.getText(key);
    }

    /**
     * @param projectCategory Project Category to assign to Project.
     * @param project         Project.
     */
    public void assign(@Nonnull final ProjectCategory projectCategory, @Nonnull final Project project) {
        PROJECT_MANAGER.setProjectCategory(project, projectCategory);
    }
}
