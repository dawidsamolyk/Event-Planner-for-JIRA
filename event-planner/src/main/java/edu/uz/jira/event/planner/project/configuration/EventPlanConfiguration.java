package edu.uz.jira.event.planner.project.configuration;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.util.Validator;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

/**
 * Configuration of the Event Plan Organization.
 */
public class EventPlanConfiguration {
    private final static Validator VALIDATOR = new Validator();
    private final Project project;
    private final String eventPlanTemplateName;
    private final String eventDueDate;

    /**
     * Constructor.
     *
     * @param request Http request.
     * @throws NullArgumentException Thrown when any required argument is null.
     */
    public EventPlanConfiguration(@Nonnull final HttpServletRequest request) throws NullArgumentException {
        VALIDATOR.check(request);

        eventPlanTemplateName = request.getParameter("event-type");
        eventDueDate = request.getParameter("event-duedate");
        String projectKey = request.getParameter("project-key");

        project = ComponentAccessor.getProjectManager().getProjectObjByKey(projectKey);
    }

    /**
     * @return Project.
     */
    public Project getProject() {
        return project;
    }

    /**
     * @return Event type.
     */
    public String getEventPlanTemplateName() {
        return eventPlanTemplateName;
    }

    /**
     * @return Event date.
     */
    public String getEventDueDate() {
        return eventDueDate;
    }
}
