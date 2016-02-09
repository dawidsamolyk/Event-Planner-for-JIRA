package edu.uz.jira.event.planner.database.xml.converter;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.database.xml.model.*;
import edu.uz.jira.event.planner.exception.EmptyComponentsListException;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.project.ProjectUtils;
import edu.uz.jira.event.planner.project.plan.rest.manager.EventPlanRestManager;
import edu.uz.jira.event.planner.util.DatesDifference;
import edu.uz.jira.event.planner.util.DatesDifferenceCalculator;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Converts JIRA Project to Event Plan template.
 */
public class ProjectToTemplateConverter {
    private final ProjectComponentManager projectComponentManager;
    private final IssueService issueService;
    private final JiraAuthenticationContext authenticationContext;
    private final ProjectUtils projectUtils;
    private final DatesDifferenceCalculator datesDifferenceCalculator;

    /**
     * Constructor.
     *
     * @param i18nResolver Injected {@code I18nResolver} implementation.
     */
    public ProjectToTemplateConverter(@Nonnull final I18nResolver i18nResolver) {
        projectUtils = new ProjectUtils(i18nResolver);
        projectComponentManager = ComponentAccessor.getProjectComponentManager();
        issueService = ComponentAccessor.getIssueService();
        authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
        datesDifferenceCalculator = new DatesDifferenceCalculator();
    }

    /**
     * @param project       Source project.
     * @param configuration Destination Event Plan template configuration.
     * @return Event Plan template.
     * @throws EmptyComponentsListException Thrown when Project Components list is empty (then Event Plan template will be empty).
     * @throws NullArgumentException        Thrown when any required argument is null.
     */
    public PlanTemplate getEventPlanTemplate(final Project project, final EventPlanRestManager.NewPlanTemplateConfiguration configuration) throws EmptyComponentsListException, NullArgumentException {
        if (project == null || configuration == null) {
            throw new NullArgumentException();
        }
        PlanTemplate result = new PlanTemplate();

        result.setName(configuration.getName());
        result.setDescription(configuration.getDescription());
        result.setReserveTimeInDays(Integer.parseInt(configuration.getReserveTime()));

        List<EventCategory> categories = getEventCategories(configuration);
        result.setEventCategory(categories);

        List<ComponentTemplate> components = getComponentTemplates(project);
        result.setComponent(components);

        if (components.isEmpty()) {
            throw new EmptyComponentsListException();
        }

        return result;
    }

    private List<ComponentTemplate> getComponentTemplates(@Nonnull final Project project) throws NullArgumentException {
        List<ComponentTemplate> components = new ArrayList<ComponentTemplate>();
        for (ProjectComponent eachComponent : project.getProjectComponents()) {
            ComponentTemplate eachComponentTemplate = new ComponentTemplate();
            eachComponentTemplate.setName(eachComponent.getName());
            eachComponentTemplate.setDescription(eachComponent.getDescription());

            List<TaskTemplate> tasks = getTaskTemplates(eachComponent, projectUtils.getDueDateVersion(project));
            eachComponentTemplate.setTask(tasks);

            components.add(eachComponentTemplate);
        }
        return components;
    }

    private List<TaskTemplate> getTaskTemplates(@Nonnull final ProjectComponent eachComponent, @Nonnull final Version dueDateVersion) throws NullArgumentException {
        List<TaskTemplate> result = new ArrayList<TaskTemplate>();

        for (Long issueId : projectComponentManager.getIssueIdsWithComponent(eachComponent)) {
            Issue issue = issueService.getIssue(authenticationContext.getUser(), issueId).getIssue();

            TaskTemplate taskTemplate = new TaskTemplate();
            taskTemplate.setName(issue.getSummary());
            taskTemplate.setDescription(issue.getDescription());

            setTaskTemplateNeededTimeBeforeEvent(dueDateVersion, issue, taskTemplate);
            taskTemplate.setSubTask(getSubTaskTemplates(issue));

            result.add(taskTemplate);
        }

        return result;
    }

    private void setTaskTemplateNeededTimeBeforeEvent(@Nonnull final Version dueDateVersion, @Nonnull final Issue issue, @Nonnull TaskTemplate taskTemplate) throws NullArgumentException {
        DatesDifference difference = datesDifferenceCalculator.calculate(issue.getDueDate(), dueDateVersion.getReleaseDate());

        taskTemplate.setNeededMonthsBeforeEvent(difference.getMonths());
        taskTemplate.setNeededDaysBeforeEvent(difference.getDays());
    }

    private List<SubTaskTemplate> getSubTaskTemplates(@Nonnull final Issue issue) {
        List<SubTaskTemplate> result = new ArrayList<SubTaskTemplate>();

        for (Issue eachSubTask : issue.getSubTaskObjects()) {
            SubTaskTemplate subTask = new SubTaskTemplate();

            subTask.setName(eachSubTask.getSummary());
            subTask.setDescription(eachSubTask.getDescription());

            result.add(subTask);
        }

        return result;
    }

    private List<EventCategory> getEventCategories(@Nonnull final EventPlanRestManager.NewPlanTemplateConfiguration configuration) {
        List<EventCategory> categories = new ArrayList<EventCategory>();
        for (String eachCategoryName : configuration.getCategories()) {
            EventCategory eachCategory = new EventCategory();
            eachCategory.setName(eachCategoryName);
            categories.add(eachCategory);
        }
        return categories;
    }
}
