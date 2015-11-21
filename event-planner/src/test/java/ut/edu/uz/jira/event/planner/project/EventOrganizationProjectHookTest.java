package ut.edu.uz.jira.event.planner.project;

import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.blueprint.api.ConfigureData;
import com.atlassian.jira.blueprint.api.ConfigureResponse;
import com.atlassian.jira.blueprint.api.ValidateData;
import com.atlassian.jira.blueprint.api.ValidateResponse;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.layout.field.EditableDefaultFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.opensymphony.workflow.loader.ActionDescriptor;
import edu.uz.jira.event.planner.project.EventOrganizationProjectHook;
import edu.uz.jira.event.planner.workflow.WorkflowConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by Dawid on 2015-11-03.
 */
public class EventOrganizationProjectHookTest {
    private WorkflowTransitionService mockWorkflowTransitionService;

    @Before
    public void setUp() {
        FieldLayoutManager mockFieldLayoutManager = Mockito.mock(FieldLayoutManager.class);
        Mockito.when(mockFieldLayoutManager.getEditableDefaultFieldLayout()).thenReturn(Mockito.mock(EditableDefaultFieldLayout.class));
        Mockito.when(mockFieldLayoutManager.createFieldLayoutScheme(Mockito.anyString(), Mockito.anyString())).thenReturn(Mockito.mock(FieldLayoutScheme.class));

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, Mockito.mock(ComponentAccessor.class))
                .addMock(FieldLayoutManager.class, mockFieldLayoutManager)
                .init();

        mockWorkflowTransitionService = Mockito.mock(WorkflowTransitionService.class);
    }

    @Test
    public void validateResponseShouldNotBeNull() {
        EventOrganizationProjectHook hook = new EventOrganizationProjectHook(mockWorkflowTransitionService);
        ApplicationUser user = mock(ApplicationUser.class);
        ValidateData validateData = new ValidateData("EVENT PLAN", "EVENT", user);

        ValidateResponse result = hook.validate(validateData);

        assertNotNull(result);
    }

    @Test
    public void configureResponseShouldNotBeNull() {
        EventOrganizationProjectHook hook = new EventOrganizationProjectHook(mockWorkflowTransitionService);
        ConfigureData configureData = ConfigureData.create(mock(Project.class), mock(Scheme.class), new HashMap<String, JiraWorkflow>(), mock(FieldConfigScheme.class), new HashMap<String, IssueType>());

        ConfigureResponse result = hook.configure(configureData);

        assertNotNull(result);
    }

    @Test
    public void eventOrganizationWorkflowShouldHasAddedUpdateDueDatePostFunction() {
        EventOrganizationProjectHook hook = new EventOrganizationProjectHook(mockWorkflowTransitionService);
        JiraWorkflow mockWorkflow = Mockito.mock(JiraWorkflow.class);
        HashMap<String, JiraWorkflow> createdWorkflows = new HashMap<String, JiraWorkflow>();
        createdWorkflows.put("EVENT-ORGANIZATION-WORKFLOW", mockWorkflow);
        ConfigureData configureData = ConfigureData.create(mock(Project.class), mock(Scheme.class), createdWorkflows, mock(FieldConfigScheme.class), new HashMap<String, IssueType>());

        hook.configure(configureData);

        for (ActionDescriptor eachWorkflowAction : mockWorkflow.getActionsByName("Deadline exceeded")) {
            assertTrue(eachWorkflowAction.getPostFunctions().contains(WorkflowConfigurator.createUpdateDueDatePostFunctionDescriptor()));
        }
    }
}
