package edu.uz.jira.event.planner.project.configuration;

import com.atlassian.jira.JiraException;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.AssigneeTypes;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.util.text.Internationalization;
import edu.uz.jira.event.planner.util.text.TextUtils;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Configures and initializes JIRA Project basing on Event Organization Plan template.
 */
public class ProjectConfigurator {
    public static final String DUE_DATE_FORMAT = "dd-MM-yyyy HH:mm";
    private final I18nResolver internationalization;
    private final IssueService issueService;
    private final JiraAuthenticationContext authenticationContext;
    private final ProjectComponentManager projectComponentManager;
    private final VersionManager versionManager;
    private final ApplicationProperties applicationProperties;
    private SubTaskManager subTaskManager;

    /**
     * Constructor.
     *
     * @param i18nResolver Injected {@code I18nResolver} implementation.
     */
    public ProjectConfigurator(@Nonnull final I18nResolver i18nResolver) {
        internationalization = i18nResolver;
        authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
        issueService = ComponentAccessor.getIssueService();
        projectComponentManager = ComponentAccessor.getProjectComponentManager();
        versionManager = ComponentAccessor.getVersionManager();
        applicationProperties = ComponentAccessor.getApplicationProperties();
        subTaskManager = ComponentAccessor.getSubTaskManager();
    }

    /**
     * @param project      Project.
     * @param eventDueDate Event date.
     * @return Created version.
     * @throws ParseException  Thrown when cannot parse event date.
     * @throws CreateException Thrown when cannot create Project Version.
     */
    public Version createVersion(@Nonnull final Project project, @Nonnull final String eventDueDate) throws ParseException, CreateException {
        if (project == null || StringUtils.isBlank(eventDueDate)) {
            return null;
        }
        String name = getInternationalized(Internationalization.PROJECT_VERSION_NAME);
        String description = getInternationalized(Internationalization.PROJECT_VERSION_DESCRIPTION);

        DateFormat format = new SimpleDateFormat(DUE_DATE_FORMAT, authenticationContext.getLocale());
        Date releaseDate = format.parse(eventDueDate);
        Long projectId = project.getId();
        Long scheduleAfterVersion = null;

        return versionManager.createVersion(name, releaseDate, description, projectId, scheduleAfterVersion);
    }

    private String getInternationalized(final String key) {
        return internationalization.getText(key);
    }

    private IssueType getIssueType(@Nonnull final Project project, final boolean subTask) throws JiraException {
        for (IssueType eachIssueType : project.getIssueTypes()) {
            if (eachIssueType.isSubTask() == subTask) {
                return eachIssueType;
            }
        }
        throw new JiraException();
    }

    /**
     * Creates Project Components, Tasks and Sub-Tasks basing on selected Event Organization Plan.
     *
     * @param project   Project.
     * @param version   Project Due Date Version.
     * @param eventPlan Event Organization Plan.
     * @return Configured Project.
     * @throws JiraException
     */
    public List<Issue> configure(@Nonnull final Project project, @Nonnull final Version version, @Nonnull final Plan eventPlan) throws JiraException {
        List<Issue> result = new ArrayList<Issue>();
        if (project == null || version == null || eventPlan == null) {
            return result;
        }
        for (Component eachComponent : eventPlan.getComponents()) {
            Task[] eachComponentTasks = eachComponent.getTasks();

            if (eachComponentTasks.length > 0) {
                ProjectComponent eachCreatedComponent = projectComponentManager.create(eachComponent.getName(), eachComponent.getDescription(), project.getLeadUserKey(), AssigneeTypes.PROJECT_DEFAULT, project.getId());

                List<Issue> createdTasks = createTasks(project, eachComponentTasks, eachCreatedComponent.getId(), version);
                result.addAll(createdTasks);
            }
        }
        return result;
    }

    private List<Issue> createTasks(@Nonnull final Project project, @Nonnull final Task[] tasks, @Nonnull final Long componentId, @Nonnull final Version version) throws JiraException {
        List<Issue> result = new ArrayList<Issue>();
        String issueTypeId = getIssueType(project, false).getId();
        Long projectId = project.getId();
        ApplicationUser user = getUser();
        String userKey = user.getKey();

        for (Task eachTask : tasks) {
            String dueDate = getFormattedDueDate(eachTask, version.getReleaseDate());

            IssueInputParameters inputParameters =
                    issueService.newIssueInputParameters()
                            .setIssueTypeId(issueTypeId)
                            .setProjectId(projectId)
                            .setComponentIds(componentId)
                            .setDueDate(dueDate)
                            .setSummary(eachTask.getName())
                            .setDescription(eachTask.getDescription())
                            .setFixVersionIds(version.getId())
                            .setReporterId(userKey)
                            .setAssigneeId(userKey);

            IssueService.CreateValidationResult validationResult = issueService.validateCreate(user, inputParameters);
            Issue task = createIssue(validationResult);
            result.add(task);

            SubTask[] eachTaskSubTasks = eachTask.getSubTasks();
            if (eachTaskSubTasks.length > 0 && task != null) {
                List<Issue> subTasks = createSubTasks(project, eachTaskSubTasks, task.getId(), componentId, version.getId(), dueDate);
                createLinks(task, subTasks);
            }
        }
        return result;
    }

    private List<Issue> createSubTasks(@Nonnull final Project project, @Nonnull final SubTask[] subTasks, @Nonnull final Long taskId, @Nonnull final Long componentId, @Nonnull final Long versionId, @Nonnull final String dueDate) throws JiraException {
        List<Issue> result = new ArrayList<Issue>();
        String issueTypeId = getIssueType(project, true).getId();
        Long projectId = project.getId();
        ApplicationUser user = getUser();
        String userKey = user.getKey();

        for (SubTask eachSubTask : subTasks) {
            IssueInputParameters inputParameters =
                    issueService.newIssueInputParameters()
                            .setIssueTypeId(issueTypeId)
                            .setProjectId(projectId)
                            .setComponentIds(componentId)
                            .setDueDate(dueDate)
                            .setSummary(eachSubTask.getName())
                            .setDescription(eachSubTask.getDescription())
                            .setFixVersionIds(versionId)
                            .setReporterId(userKey)
                            .setAssigneeId(userKey);

            IssueService.CreateValidationResult validationResult = issueService.validateSubTaskCreate(user, taskId, inputParameters);
            Issue subTask = createIssue(validationResult);
            result.add(subTask);
        }
        return result;
    }

    private void createLinks(@Nonnull final Issue task, @Nonnull final List<Issue> subTasks) throws CreateException {
        for (Issue eachSubTask : subTasks) {
            subTaskManager.createSubTaskIssueLink(task, eachSubTask, getUser().getDirectoryUser());
        }
    }

    private Issue createIssue(@Nonnull final IssueService.CreateValidationResult validationResult) throws JiraException {
        if (validationResult != null && validationResult.isValid()) {
            return issueService.create(getUser(), validationResult).getIssue();
        } else {
            Collection<String> errorMessages = validationResult.getErrorCollection().getErrorMessages();
            throw new JiraException(TextUtils.getJoined(errorMessages, ' '));
        }
    }

    private ApplicationUser getUser() {
        return authenticationContext.getUser();
    }

    private String getFormattedDueDate(@Nonnull final TimeFramedEntity entity, @Nonnull final Date releaseDate) {
        Calendar calendar = Calendar.getInstance(authenticationContext.getLocale());
        calendar.setTime(releaseDate);
        calendar.add(Calendar.MONTH, -entity.getNeededMonthsToComplete());
        calendar.add(Calendar.DATE, -entity.getNeededDaysToComplete());

        Date resultDueDate = calendar.getTime();

        String dateFormat = applicationProperties.getDefaultBackedString(APKeys.JIRA_LF_DATE_DMY);
        return new SimpleDateFormat(dateFormat).format(resultDueDate);
    }

}
