package edu.uz.jira.event.planner.project;

import com.atlassian.jira.blueprint.api.*;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.sal.api.message.I18nResolver;

public class EventOrganizationProjectHook implements AddProjectHook  {
    private final I18nResolver i18nResolver;

    public EventOrganizationProjectHook(I18nResolver i18nResolver){
        this.i18nResolver = i18nResolver;
    }

    @Override
    public ValidateResponse validate(final ValidateData validateData) {
        return ValidateResponse.create();
    }

    @Override
    public ConfigureResponse configure(final ConfigureData configureData) {
        JiraWorkflow workflow = configureData.createdWorkflows().get(i18nResolver.getText("project.workflow.name"));


        String redirect = "/browse/" + configureData.project().getKey() + "#selectedTab=com.atlassian.jira.plugin.system.project%3Asummary-panel";
        return ConfigureResponse.create().setRedirect(redirect);
    }
}
