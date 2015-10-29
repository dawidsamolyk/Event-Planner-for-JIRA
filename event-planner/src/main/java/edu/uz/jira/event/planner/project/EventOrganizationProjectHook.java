package edu.uz.jira.event.planner.project;

import com.atlassian.jira.blueprint.api.AddProjectHook;
import com.atlassian.jira.blueprint.api.ConfigureData;
import com.atlassian.jira.blueprint.api.ConfigureResponse;
import com.atlassian.jira.blueprint.api.ValidateData;
import com.atlassian.jira.blueprint.api.ValidateResponse;
import com.atlassian.jira.issue.issuetype.IssueType;

import java.util.Map;

public class EventOrganizationProjectHook implements AddProjectHook {
    @Override
    public ValidateResponse validate(final ValidateData validateData) {
        ValidateResponse validateResponse = ValidateResponse.create();

        return validateResponse;
    }

    @Override
    public ConfigureResponse configure(final ConfigureData configureData) {
        String redirect = "/browse/" + configureData.project().getKey() + "#selectedTab=com.atlassian.jira.plugin.system.project%3Asummary-panel";

        Map<String, IssueType> createdIssueTypes = configureData.createdIssueTypes();
        

        ConfigureResponse configureResponse = ConfigureResponse.create().setRedirect(redirect);

        return configureResponse;
    }
}
