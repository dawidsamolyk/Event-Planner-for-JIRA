package edu.uz.jira.event.planner.project.configuration;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.exceptions.NullArgumentException;
import edu.uz.jira.event.planner.utils.InternationalizationKeys;
import webwork.action.Action;

import javax.annotation.Nonnull;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Webwork action for configuring Event Plan Organization project.
 */
public class EventPlanConfigWebworkAction extends JiraWebActionSupport {
    public static final String DUE_DATE_FORMAT = "dd-MM-yyyy HH:mm";
    private static final VersionManager VERSION_MANAGER = ComponentAccessor.getVersionManager();
    private final I18nResolver i18nResolver;

    /**
     * @param i18nResolver Internationalization resolver.
     */
    public EventPlanConfigWebworkAction(@Nonnull final I18nResolver i18nResolver) {
        this.i18nResolver = i18nResolver;
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

        if (EventPlanConfigurationValidator.isInvalid(project)) {
            return Action.ERROR;

        } else if (EventPlanConfigurationValidator.canInputProjectConfiguration(project, eventType, eventDueDate)) {
            return Action.INPUT;

        } else if (EventPlanConfigurationValidator.canConfigureProject(project, eventType, eventDueDate)) {
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
        String name = getInternationalized(InternationalizationKeys.PROJECT_VERSION_NAME);
        String description = getInternationalized(InternationalizationKeys.PROJECT_VERSION_DESCRIPTION);
        Date startDate = new Date();
        DateFormat format = new SimpleDateFormat(DUE_DATE_FORMAT);
        Date releaseDate = format.parse(eventDueDate);
        Long projectId = project.getId();

        Version version = VERSION_MANAGER.createVersion(name, startDate, releaseDate, description, projectId, null);
    }

    private String getInternationalized(String key) {
        return i18nResolver.getText(key);
    }
}
