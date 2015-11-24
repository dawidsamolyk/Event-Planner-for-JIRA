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
import com.opensymphony.workflow.loader.FunctionDescriptor;
import edu.uz.jira.event.planner.project.EventOrganizationProjectHook;
import edu.uz.jira.event.planner.workflow.WorkflowConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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
        final String transitionName = "Deadline exceeded";
        final FunctionDescriptor postFunctionDescriptor = WorkflowConfigurator.createUpdateDueDatePostFunctionDescriptor();

        final JiraWorkflow mockWorkflow = mock(JiraWorkflow.class);

        ActionDescriptor mockAction = mock(ActionDescriptor.class);
        Mockito.when(mockAction.getName()).thenReturn(transitionName);
        final List mockPostFunctions = new ArrayList();
        Mockito.when(mockAction.getPostFunctions()).thenReturn(mockPostFunctions);

        Collection<ActionDescriptor> mockActionDescriptors = new ArrayList<ActionDescriptor>();
        mockActionDescriptors.add(mockAction);
        Mockito.when(mockWorkflow.getActionsByName(transitionName)).thenReturn(mockActionDescriptors);

        Mockito.when(mockWorkflowTransitionService.addPostFunctionToWorkflow(Mockito.eq(transitionName), (FunctionDescriptor) Mockito.anyObject(), (JiraWorkflow) Mockito.anyObject()))
                .then(new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        Object[] arguments = invocation.getArguments();
                        if (arguments[0].equals(transitionName) && arguments[2].equals(mockWorkflow)) {
                            mockPostFunctions.add(postFunctionDescriptor);
                        }
                        return null;
                    }
                });

        HashMap<String, JiraWorkflow> createdWorkflows = new HashMap<String, JiraWorkflow>();
        createdWorkflows.put("EVENT-ORGANIZATION-WORKFLOW", mockWorkflow);
        ConfigureData configureData = ConfigureData.create(mock(Project.class), mock(Scheme.class), createdWorkflows, mock(FieldConfigScheme.class), new HashMap<String, IssueType>());

        EventOrganizationProjectHook hook = new EventOrganizationProjectHook(mockWorkflowTransitionService);

        hook.configure(configureData);

        for (ActionDescriptor eachWorkflowAction : mockWorkflow.getActionsByName(transitionName)) {
            assertTrue(eachWorkflowAction.getPostFunctions().contains(postFunctionDescriptor));
        }
    }
}
