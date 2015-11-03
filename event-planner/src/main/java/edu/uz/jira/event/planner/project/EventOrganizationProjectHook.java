package edu.uz.jira.event.planner.project;

import com.atlassian.jira.blueprint.api.AddProjectHook;
import com.atlassian.jira.blueprint.api.ConfigureData;
import com.atlassian.jira.blueprint.api.ConfigureResponse;
import com.atlassian.jira.blueprint.api.ValidateData;
import com.atlassian.jira.blueprint.api.ValidateResponse;
import com.atlassian.jira.project.Project;

public class EventOrganizationProjectHook implements AddProjectHook {
    @Override
    public ValidateResponse validate(final ValidateData validateData) {
        ValidateResponse validateResponse = ValidateResponse.create();

        return validateResponse;
    }

    @Override
    public ConfigureResponse configure(final ConfigureData configureData) {
        Project project = configureData.project();

        String redirect = "/browse/" + project.getKey() + "#selectedTab=com.atlassian.jira.plugin.system.project%3Asummary-panel";
        ConfigureResponse configureResponse = ConfigureResponse.create().setRedirect(redirect);

        return configureResponse;
    }
}
