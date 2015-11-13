package edu.uz.jira.event.planner.project;

import com.atlassian.jira.blueprint.api.*;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowUtil;
import com.opensymphony.workflow.loader.ActionDescriptor;
import edu.uz.jira.event.planner.workflow.UpdateDueDatePostFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class EventOrganizationProjectHook implements AddProjectHook {
    private final static Logger LOGGER = LoggerFactory.getLogger(EventOrganizationProjectHook.class);

    @Override
    public ValidateResponse validate(final ValidateData validateData) {
        return ValidateResponse.create();
    }

    @Override
    public ConfigureResponse configure(final ConfigureData configureData) {
        String redirect = "/browse/" + configureData.project().getKey() + "#selectedTab=com.atlassian.jira.plugin.system.project%3Asummary-panel";
        return ConfigureResponse.create().setRedirect(redirect);
    }
}
