package edu.uz.jira.event.planner.util.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import webwork.action.Action;

/**
 * Webwork action which always return INPUT.
 */
public class OnlyShowInputAction extends JiraWebActionSupport {

    /**
     * @return Result view to show.
     * @throws Exception Thrown when any error occurs.
     */
    @Override
    public String execute() throws Exception {
        return Action.INPUT;
    }
}
