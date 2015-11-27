package edu.uz.jira.event.planner.project.configuration;

import com.atlassian.jira.project.Project;
import org.apache.commons.lang3.StringUtils;

public class ConfigValidator {
    public static boolean containsAnyVersion(final Project project) {
        if (project == null) {
            return false;
        }
        return !project.getVersions().isEmpty();
    }

    public static boolean canShowError(final Project project) {
        return project == null || containsAnyVersion(project);
    }

    public static boolean canInputProjectConfiguration(final Project project, final String eventType, final String eventDueDate) {
        return project != null && !containsAnyVersion(project)
                && StringUtils.isBlank(eventType) && StringUtils.isBlank(eventDueDate);
    }

    public static boolean canConfigureProject(final Project project, final String eventType, final String eventDueDate) {
        return project != null && !containsAnyVersion(project)
                && StringUtils.isNotBlank(eventType) && StringUtils.isNotBlank(eventDueDate);
    }
}
