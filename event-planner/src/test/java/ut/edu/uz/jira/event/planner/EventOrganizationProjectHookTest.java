package ut.edu.uz.jira.event.planner;

import com.atlassian.jira.blueprint.api.ConfigureData;
import com.atlassian.jira.blueprint.api.ConfigureResponse;
import com.atlassian.jira.blueprint.api.ValidateData;
import com.atlassian.jira.blueprint.api.ValidateResponse;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.JiraWorkflow;
import edu.uz.jira.event.planner.project.EventOrganizationProjectHook;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.util.MockCreationValidator;

import java.util.HashMap;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by Dawid on 2015-11-03.
 */
public class EventOrganizationProjectHookTest {

    @Test
    public void validateResponseShouldNotBeNull() {
        EventOrganizationProjectHook hook = new EventOrganizationProjectHook();
        ApplicationUser user = Mockito.mock(ApplicationUser.class);
        ValidateData validateData = new ValidateData("EVENT PLAN", "EVENT", user);

        ValidateResponse result = hook.validate(validateData);

        assertNotNull(result);
    }

    @Test
    public void configureResponseShouldNotBeNull() {
        EventOrganizationProjectHook hook = new EventOrganizationProjectHook();
        ConfigureData configureData = ConfigureData.create(Mockito.mock(Project.class), Mockito.mock(Scheme.class), new HashMap<String, JiraWorkflow>(), Mockito.mock(FieldConfigScheme.class), new HashMap<String, IssueType>());

        ConfigureResponse result = hook.configure(configureData);

        assertNotNull(result);
    }
}
