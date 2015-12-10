package ut.edu.uz.jira.event.planner.project;

import com.atlassian.jira.bc.JiraServiceContext;
import com.atlassian.jira.bc.workflow.WorkflowService;
import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.blueprint.api.ConfigureData;
import com.atlassian.jira.blueprint.api.ConfigureResponse;
import com.atlassian.jira.blueprint.api.ValidateData;
import com.atlassian.jira.blueprint.api.ValidateResponse;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.layout.field.EditableDefaultFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.ofbiz.MockOfBizDelegator;
import com.atlassian.jira.mock.security.MockAuthenticationContext;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import edu.uz.jira.event.planner.exceptions.NullArgumentException;
import edu.uz.jira.event.planner.project.EventOrganizationProjectHook;
import edu.uz.jira.event.planner.util.Internationalization;
import org.apache.commons.collections.MapUtils;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

public class EventOrganizationProjectHookTest {
    private I18nResolver mocki18n;
    private WorkflowTransitionService mockWorkflowTransitionService;
    private WorkflowService mockWorkflowService;
    private WorkflowManager mockWorkflowManager;

    @Before
    public void setUp() {
        FieldLayoutManager mockFieldLayoutManager = Mockito.mock(FieldLayoutManager.class);
        Mockito.when(mockFieldLayoutManager.getEditableDefaultFieldLayout()).thenReturn(Mockito.mock(EditableDefaultFieldLayout.class));
        Mockito.when(mockFieldLayoutManager.createFieldLayoutScheme(Mockito.anyString(), Mockito.anyString())).thenReturn(Mockito.mock(FieldLayoutScheme.class));
        Mockito.when(mockFieldLayoutManager.storeAndReturnEditableFieldLayout(Mockito.any(EditableFieldLayout.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                EditableFieldLayout layout = (EditableFieldLayout) invocation.getArguments()[0];
                return layout;
            }
        });

        mocki18n = mock(I18nResolver.class);
        Mockito.when(mocki18n.getText(Internationalization.PROJECT_FIELDS_CONFIGURATION_NAME)).thenReturn("Event organization Field Configuration");
        Mockito.when(mocki18n.getText(Internationalization.PROJECT_FIELDS_CONFIGURATION_DESCRIPTION)).thenReturn("Field Configuration for the Event organization Issues");

        final JiraWorkflow mockDraft = mock(JiraWorkflow.class);
        Collection<ActionDescriptor> actions = new ArrayList<ActionDescriptor>();
        Mockito.when(mockDraft.getActionsByName(Mockito.anyString())).thenReturn(actions);
        mockWorkflowManager = mock(WorkflowManager.class);
        Mockito.when(mockWorkflowManager.getDraftWorkflow(Mockito.anyString())).thenReturn(mockDraft);

        mockWorkflowTransitionService = mock(WorkflowTransitionService.class);
        mockWorkflowService = Mockito.mock(WorkflowService.class);

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, Mockito.mock(ComponentAccessor.class))
                .addMock(FieldLayoutManager.class, mockFieldLayoutManager)
                .addMock(ProjectManager.class, Mockito.mock(ProjectManager.class))
                .addMock(WorkflowService.class, mockWorkflowService)
                .addMock(OfBizDelegator.class, new MockOfBizDelegator())
                .addMock(WorkflowManager.class, mockWorkflowManager)
                .addMock(JiraAuthenticationContext.class, new MockAuthenticationContext(new MockApplicationUser("test")))
                .init();
    }

    @Test
    public void validateResponseShouldNotBeNull() throws NullArgumentException {
        EventOrganizationProjectHook hook = new EventOrganizationProjectHook(mocki18n, mockWorkflowTransitionService);
        ValidateData validateData = new ValidateData("EVENT PLAN", "EVENT", mock(ApplicationUser.class));

        ValidateResponse result = hook.validate(validateData);

        assertNotNull(result);
    }

    @Test
    public void configureResponseShouldNotBeNull() throws NullArgumentException {
        EventOrganizationProjectHook hook = new EventOrganizationProjectHook(mocki18n, mockWorkflowTransitionService);
        ConfigureData configureData = ConfigureData.create(mock(Project.class), mock(Scheme.class), new HashMap<String, JiraWorkflow>(), mock(FieldConfigScheme.class), new HashMap<String, IssueType>());

        ConfigureResponse result = hook.configure(configureData);

        assertNotNull(result);
    }

    @Test
    public void eventOrganizationWorkflowShouldHasAddedUpdateDueDatePostFunction() throws NullArgumentException {
        final String transitionName = "Deadline exceeded";
        String mockWorkflowName = "EVENT-ORGANIZATION-WORKFLOW";

        final JiraWorkflow mockWorkflow = mock(JiraWorkflow.class);
        Mockito.when(mockWorkflow.getName()).thenReturn(mockWorkflowName);

        ActionDescriptor mockAction = mock(ActionDescriptor.class);
        Mockito.when(mockAction.getName()).thenReturn(transitionName);
        final List<FunctionDescriptor> mockPostFunctions = new ArrayList<FunctionDescriptor>();
        Mockito.when(mockAction.getPostFunctions()).thenReturn(mockPostFunctions);

        Collection<ActionDescriptor> mockActionDescriptors = new ArrayList<ActionDescriptor>();
        mockActionDescriptors.add(mockAction);
        Mockito.when(mockWorkflow.getActionsByName(transitionName)).thenReturn(mockActionDescriptors);
        HashMap<String, JiraWorkflow> createdWorkflows = new HashMap<String, JiraWorkflow>();

        createdWorkflows.put(mockWorkflowName, mockWorkflow);
        ConfigureData configureData = ConfigureData.create(mock(Project.class), mock(Scheme.class), createdWorkflows, mock(FieldConfigScheme.class), new HashMap<String, IssueType>());

        Mockito.when(mockWorkflowService.getDraftWorkflow(Mockito.any(JiraServiceContext.class), Mockito.anyString())).thenReturn(mockWorkflow);
        Mockito.when(mockWorkflowService.createDraftWorkflow(Mockito.any(JiraServiceContext.class), Mockito.anyString())).thenReturn(mockWorkflow);

        Mockito.when(mockWorkflowTransitionService.addConditionToWorkflow(Mockito.anyString(), Mockito.any(ConditionDescriptor.class), Mockito.any(JiraWorkflow.class))).thenReturn(new SimpleErrorCollection());
        Mockito.when(mockWorkflowTransitionService.addPostFunctionToWorkflow(Mockito.anyString(), Mockito.any(FunctionDescriptor.class), Mockito.any(JiraWorkflow.class))).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        Object[] arguments = invocation.getArguments();
                        String transitionName = (String) arguments[0];
                        FunctionDescriptor function = (FunctionDescriptor) arguments[1];
                        JiraWorkflow workflow = (JiraWorkflow) arguments[2];

                        for (ActionDescriptor each : workflow.getActionsByName(transitionName)) {
                            each.getPostFunctions().add(0, function);
                        }

                        return new SimpleErrorCollection();
                    }
                });

        EventOrganizationProjectHook hook = new EventOrganizationProjectHook(mocki18n, mockWorkflowTransitionService);

        hook.configure(configureData);

        for (ActionDescriptor eachWorkflowAction : mockWorkflow.getActionsByName(transitionName)) {
            assertFalse(eachWorkflowAction.getPostFunctions().isEmpty());
        }
    }

    @Test
    public void shouldReturnRedirectWithoutArgumentsWhenErrorOccursDuringProjectConfiguration() throws NullArgumentException {
        EventOrganizationProjectHook hook = new EventOrganizationProjectHook(mocki18n, mockWorkflowTransitionService);
        ConfigureData configureData = ConfigureData.create(mock(Project.class), mock(Scheme.class), MapUtils.EMPTY_MAP, mock(FieldConfigScheme.class), new HashMap<String, IssueType>());

        ConfigureResponse response = hook.configure(configureData);

        assertEquals(EventOrganizationProjectHook.REDIRECT_URL, response.getRedirect());
    }
}
