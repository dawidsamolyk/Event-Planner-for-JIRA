package edu.uz.jira.event.planner.project;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webwork.action.Action;

import javax.servlet.http.HttpServletRequest;

public class EventOrganizationConfigWebworkAction extends JiraWebActionSupport {
    private static final Logger log = LoggerFactory.getLogger(EventOrganizationConfigWebworkAction.class);

    @Override
    public String execute() throws Exception {
        HttpServletRequest request = getHttpRequest();
        String eventType = request.getParameter("event-type");
        String eventDueDate = request.getParameter("event-duedate");

        if (StringUtils.isBlank(eventType) && StringUtils.isBlank(eventDueDate)) {
            return Action.INPUT;
        } else if (StringUtils.isNotBlank(eventType) && StringUtils.isNotBlank(eventDueDate)) {
            //TODO zastosuj konfigurację

            return Action.SUCCESS;
        }
        return Action.ERROR;
    }
}
