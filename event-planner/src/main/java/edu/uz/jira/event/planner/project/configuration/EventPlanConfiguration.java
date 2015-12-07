package edu.uz.jira.event.planner.project.configuration;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import edu.uz.jira.event.planner.exceptions.NullArgumentException;
import edu.uz.jira.event.planner.utils.Validator;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

/**
 * Configuration of the Event Plan Organization.
 */
public class EventPlanConfiguration {
    private final Project PROJECT;
    private final String EVENT_TYPE;
    private final String EVENT_DUE_DATE;

    /**
     * @param request Http request.
     * @throws NullArgumentException
     */
    public EventPlanConfiguration(@Nonnull final HttpServletRequest request) throws NullArgumentException {
        Validator.check(request);

        EVENT_TYPE = request.getParameter("event-type");
        EVENT_DUE_DATE = request.getParameter("event-duedate");
        String projectKey = request.getParameter("project-key");

        PROJECT = ComponentAccessor.getProjectManager().getProjectObjByKey(projectKey);
    }

    /**
     * @return Project.
     */
    public Project getProject() {
        return PROJECT;
    }

    /**
     * @return Event type.
     */
    public String getEventType() {
        return EVENT_TYPE;
    }

    /**
     * @return Event date.
     */
    public String getEventDueDate() {
        return EVENT_DUE_DATE;
    }
}
