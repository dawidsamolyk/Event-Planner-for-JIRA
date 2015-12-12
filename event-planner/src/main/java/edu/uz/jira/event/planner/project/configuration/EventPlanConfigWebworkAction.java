package edu.uz.jira.event.planner.project.configuration;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.exceptions.NullArgumentException;
import edu.uz.jira.event.planner.project.plan.EventOrganizationPlanService;
import edu.uz.jira.event.planner.util.Internationalization;
import webwork.action.Action;

import javax.annotation.Nonnull;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Webwork action for configuring Event Plan Organization project.
 */
public class EventPlanConfigWebworkAction extends JiraWebActionSupport {
    public static final String DUE_DATE_FORMAT = "dd-MM-yyyy HH:mm";
    private final VersionManager versionManager;
    private final I18nResolver internationalization;
    private final EventPlanConfigurationValidator validator;
    private final EventOrganizationPlanService eventPlanService;

    /**
     * Constructor.
     *
     * @param i18nResolver Injected {@code I18nResolver} implementation.
     */
    public EventPlanConfigWebworkAction(@Nonnull final I18nResolver i18nResolver, @Nonnull final EventOrganizationPlanService eventPlanService) {
        internationalization = i18nResolver;
        this.eventPlanService = eventPlanService;
        versionManager = ComponentAccessor.getVersionManager();
        validator = new EventPlanConfigurationValidator();
    }

    /**
     * @return Result view to show.
     * @throws Exception Thrown when any error occurs.
     */
    @Override
    public String execute() throws Exception {
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
            setDueDateAsProjectVersion(project, eventDueDate);
            return Action.SUCCESS;
        }
        return Action.ERROR;
    }

    /**
     * @param project      Project.
     * @param eventDueDate Event date.
     * @throws ParseException  Thrown when cannot parse event date.
     * @throws CreateException Thrown when cannot create Project Version.
     */
    private void setDueDateAsProjectVersion(@Nonnull final Project project, @Nonnull final String eventDueDate) throws ParseException, CreateException {
        String name = getInternationalized(Internationalization.PROJECT_VERSION_NAME);
        String description = getInternationalized(Internationalization.PROJECT_VERSION_DESCRIPTION);
        Date startDate = new Date();
        DateFormat format = new SimpleDateFormat(DUE_DATE_FORMAT);
        Date releaseDate = format.parse(eventDueDate);
        Long projectId = project.getId();

        versionManager.createVersion(name, startDate, releaseDate, description, projectId, null);
    }

    private String getInternationalized(String key) {
        return internationalization.getText(key);
    }

    /**
     *
     * @return Event Plans with Event Organization Domain name as key (eg. Development), and Event Organization Plan name
     */
    public Map<String, List<String>> getEventPlans() {
        return eventPlanService.getEventPlansSortedByDomain();
    }
}
