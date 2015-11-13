package ut.edu.uz.jira.event.planner.project;

import com.atlassian.jira.blueprint.api.ConfigureData;
import com.atlassian.jira.blueprint.api.ConfigureResponse;
import com.atlassian.jira.blueprint.api.ValidateData;
import com.atlassian.jira.blueprint.api.ValidateResponse;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.jira.workflow.WorkflowUtil;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.project.EventOrganizationProjectHook;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Dawid on 2015-11-03.
 */
public class EventOrganizationProjectHookTest {


    @Test
    public void validateResponseShouldNotBeNull() {
        EventOrganizationProjectHook hook = new EventOrganizationProjectHook();
        ApplicationUser user = mock(ApplicationUser.class);
        ValidateData validateData = new ValidateData("EVENT PLAN", "EVENT", user);

        ValidateResponse result = hook.validate(validateData);

        assertNotNull(result);
    }

    @Test
    public void configureResponseShouldNotBeNull() {
        EventOrganizationProjectHook hook = new EventOrganizationProjectHook();
        ConfigureData configureData = ConfigureData.create(mock(Project.class), mock(Scheme.class), new HashMap<String, JiraWorkflow>(), mock(FieldConfigScheme.class), new HashMap<String, IssueType>());

        ConfigureResponse result = hook.configure(configureData);

        assertNotNull(result);
    }
}
