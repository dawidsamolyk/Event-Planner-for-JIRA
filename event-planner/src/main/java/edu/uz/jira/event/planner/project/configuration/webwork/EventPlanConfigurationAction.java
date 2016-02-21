package edu.uz.jira.event.planner.project.configuration.webwork;

import com.atlassian.jira.JiraException;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.active.objects.model.Plan;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.project.configuration.EventPlanConfiguration;
import edu.uz.jira.event.planner.project.configuration.EventPlanConfigurationValidator;
import edu.uz.jira.event.planner.project.configuration.ProjectConfigurator;
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
     * @throws JiraException         Thrown when cannot do JIRA action.
     * @throws ParseException        Thrown when cannot parse data.
     * @throws NullArgumentException Thrown when any required argument is null.
     */
    @Override
    public String execute() throws ParseException, JiraException, NullArgumentException {
        EventPlanConfiguration config;
        try {
            config = new EventPlanConfiguration(getHttpRequest());
        } catch (NullArgumentException e) {
            return Action.ERROR;
        }

        Project project = config.getProject();
        String eventDueDate = config.getEventDueDate();
        String eventPlanTemplateName = config.getEventPlanTemplateName();

        if (validator.isInvalid(project)) {
            return Action.ERROR;

        } else if (validator.canInputProjectConfiguration(project, eventPlanTemplateName, eventDueDate)) {
            return Action.INPUT;

        } else if (validator.canConfigureProject(project, eventPlanTemplateName, eventDueDate)) {
            Version version = createEventDateVersion(project, eventDueDate);
            createProjectElements(project, eventPlanTemplateName, version);
            return Action.SUCCESS;
        }
        return Action.ERROR;
    }

    private Version createEventDateVersion(@Nonnull final Project project, @Nonnull final String eventDueDate) throws ParseException, CreateException {
        return projectConfigurator.createVersion(project, eventDueDate);
    }

    private void createProjectElements(@Nonnull final Project project, @Nonnull final String eventPlanTemplateName, @Nonnull final Version version) throws ParseException, JiraException {
        List<Plan> eventPlans = activeObjectsService.get(Plan.class, Query.select().where(Plan.NAME + " = ?", eventPlanTemplateName));

        if (!eventPlans.isEmpty()) {
            projectConfigurator.configure(project, version, eventPlans.get(0));
        }
    }

    /**
     * @return Event Plan Templates with Event Organization Category name as key (eg. Development), and Event Organization Plan Template name.
     */
    public Map<String, List<String>> getEventPlans() {
        return activeObjectsService.getEventPlansSortedByDomain();
    }

    /**
     * @return Event Plans Templates names with estimated time to complete (as text) for each Plan Template.
     */
    public Map<String, Integer> getEstimatedTimeForEachPlan() {
        return activeObjectsService.getEstimatedTimeForEachPlan();
    }
}
