package edu.uz.jira.event.planner.project.configuration;

import com.atlassian.jira.JiraException;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.project.plan.ActiveObjectsService;
import edu.uz.jira.event.planner.project.plan.ProjectConfigurator;
import edu.uz.jira.event.planner.project.plan.model.Plan;
import net.java.ao.Query;
import webwork.action.Action;

import javax.annotation.Nonnull;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * Webwork action for configuring Event Plan Organization project.
 */
public class EventPlanConfigurationAction extends JiraWebActionSupport {
    private final EventPlanConfigurationValidator validator;
    private final ProjectConfigurator projectConfigurator;
    private final ActiveObjectsService activeObjectsService;

    /**
     * Constructor.
     *
     * @param i18nResolver Injected {@code I18nResolver} implementation.
     */
    public EventPlanConfigurationAction(@Nonnull final I18nResolver i18nResolver,
                                        @Nonnull final ActiveObjectsService activeObjectsService) {
        this.activeObjectsService = activeObjectsService;
        projectConfigurator = new ProjectConfigurator(i18nResolver);
        validator = new EventPlanConfigurationValidator();
    }

    /**
     * @return Result view to show.
     * @throws Exception Thrown when any error occurs.
     */
    @Override
    public String execute() throws ParseException, JiraException {
        EventPlanConfiguration config;
        try {
            config = new EventPlanConfiguration(getHttpRequest());
        } catch (NullArgumentException e) {
            return Action.ERROR;
        }

        Project project = config.getProject();
        String eventDueDate = config.getEventDueDate();
        String eventType = config.getEventType();

        if (validator.isInvalid(project)) {
            return Action.ERROR;

        } else if (validator.canInputProjectConfiguration(project, eventType, eventDueDate)) {
            return Action.INPUT;

        } else if (validator.canConfigureProject(project, eventType, eventDueDate)) {
            configureProject(project, eventDueDate, eventType);
            return Action.SUCCESS;
        }
        return Action.ERROR;
    }

    private void configureProject(@Nonnull final Project project, @Nonnull final String eventDueDate, @Nonnull final String eventType) throws ParseException, JiraException {
        Version version = projectConfigurator.createVersion(project, eventDueDate);

        List<Plan> eventPlans = activeObjectsService.get(Plan.class, Query.select().where(Plan.NAME + " = ?", eventType));
        if (!eventPlans.isEmpty()) {
            projectConfigurator.configure(project, version, eventPlans.get(0));
        }
    }

    /**
     * @return Event Plans with Event Organization Domain name as key (eg. Development), and Event Organization Plan name
     */
    public Map<String, List<String>> getEventPlans() {
        return activeObjectsService.getEventPlansSortedByDomain();
    }
}
