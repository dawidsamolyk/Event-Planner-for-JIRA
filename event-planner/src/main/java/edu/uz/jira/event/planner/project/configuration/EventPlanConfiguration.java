package edu.uz.jira.event.planner.project.configuration;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import edu.uz.jira.event.planner.exceptions.NullArgumentException;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

public class EventPlanConfiguration {
    private final Project PROJECT;
    private final String EVENT_TYPE;
    private final String EVENT_DUE_DATE;

    public EventPlanConfiguration(@Nonnull final HttpServletRequest request) throws NullArgumentException {
        checkArguments(request);

        EVENT_TYPE = request.getParameter("event-type");
        EVENT_DUE_DATE = request.getParameter("event-duedate");

        String projectKey = request.getParameter("project-key");
        PROJECT = ComponentAccessor.getProjectManager().getProjectObjByKey(projectKey);
    }

    private void checkArguments(final HttpServletRequest request) throws NullArgumentException {
        if (request == null) {
            throw new NullArgumentException(HttpServletRequest.class.getName());
        }
    }

    public Project getProject() {
        return PROJECT;
    }

    public String getEventType() {
        return EVENT_TYPE;
    }

    public String getEventDueDate() {
        return EVENT_DUE_DATE;
    }
}
