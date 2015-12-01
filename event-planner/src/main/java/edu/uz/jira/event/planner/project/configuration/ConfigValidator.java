package edu.uz.jira.event.planner.project.configuration;

import com.atlassian.jira.project.Project;
import org.apache.commons.lang3.StringUtils;

/**
 * Validator of Event Plan Organization configuration.
 */
public class ConfigValidator {
    /**
     * @param project Project.
     * @return Indicates that project contains any Version. If project is null then return false.
     */
    public static boolean containsAnyVersion(final Project project) {
        if (project == null) {
            return false;
        }
        return !project.getVersions().isEmpty();
    }

    /**
     * @param project Project.
     * @return Indicates that configuration is invalid (is null or contains any Version).
     */
    public static boolean isInvalid(final Project project) {
        return project == null || containsAnyVersion(project);
    }

    /**
     * @param project      Project.
     * @param eventType    Type of an event.
     * @param eventDueDate Date of an event.
     * @return Indicates that project is not configured and enables to input configuration parameters.
     */
    public static boolean canInputProjectConfiguration(final Project project, final String eventType, final String eventDueDate) {
        return project != null && !containsAnyVersion(project)
                && StringUtils.isBlank(eventType) && StringUtils.isBlank(eventDueDate);
    }

    /**
     * @param project      Project.
     * @param eventType    Type of an event.
     * @param eventDueDate Date of an event.
     * @return Indicates that project configuration was entered.
     */
    public static boolean canConfigureProject(final Project project, final String eventType, final String eventDueDate) {
        return project != null && !containsAnyVersion(project)
                && StringUtils.isNotBlank(eventType) && StringUtils.isNotBlank(eventDueDate);
    }
}
