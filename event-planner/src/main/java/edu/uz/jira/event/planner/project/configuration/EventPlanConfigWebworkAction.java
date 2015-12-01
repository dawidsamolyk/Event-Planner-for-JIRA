package edu.uz.jira.event.planner.project.configuration;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.message.I18nResolver;
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
    public static final VersionManager VERSION_MANAGER = ComponentAccessor.getVersionManager();
    private final I18nResolver i18n;

    /**
     * @param i18n Internationalization resolver.
     */
    public EventPlanConfigWebworkAction(@Nonnull final I18nResolver i18n) {
        this.i18n = i18n;
    }

    /**
     * @return Restul view to show.
     * @throws Exception Thrown when any error occurs.
     */
    @Override
    public String execute() throws Exception {
        EventPlanConfiguration config = new EventPlanConfiguration(getHttpRequest());
        Project project = config.getProject();
        String eventDueDate = config.getEventDueDate();
        String eventType = config.getEventType();

        if (ConfigValidator.isInvalid(project)) {
            return Action.ERROR;

        } else if (ConfigValidator.canInputProjectConfiguration(project, eventType, eventDueDate)) {
            return Action.INPUT;

        } else if (ConfigValidator.canConfigureProject(project, eventType, eventDueDate)) {
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
        String versionName = i18n.getText("project.version.name");
        String versionDescription = i18n.getText("project.version.description");
        Date startDate = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd", getLocale());
        Date releaseDate = format.parse(eventDueDate);
        Long projectId = project.getId();

        Version version = VERSION_MANAGER.createVersion(versionName, startDate, releaseDate, versionDescription, projectId, null);
    }
}
