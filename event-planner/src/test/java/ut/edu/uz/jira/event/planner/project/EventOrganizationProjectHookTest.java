package ut.edu.uz.jira.event.planner.project;

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
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.opensymphony.workflow.loader.ActionDescriptor;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.project.EventOrganizationProjectHook;
import edu.uz.jira.event.planner.util.text.Internationalization;
import org.apache.commons.collections.MapUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
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
    public void validate_Response_Should_Not_Be_Null() throws NullArgumentException {
        EventOrganizationProjectHook hook = new EventOrganizationProjectHook(mocki18n, mockWorkflowTransitionService);
        ValidateData validateData = new ValidateData("EVENT PLAN", "EVENT", mock(ApplicationUser.class));

        ValidateResponse result = hook.validate(validateData);

        assertNotNull(result);
    }

    @Test
    public void configure_Response_Should_Not_Be_Null() throws NullArgumentException {
        EventOrganizationProjectHook hook = new EventOrganizationProjectHook(mocki18n, mockWorkflowTransitionService);
        ConfigureData configureData = ConfigureData.create(mock(Project.class), mock(Scheme.class), new HashMap<String, JiraWorkflow>(), mock(FieldConfigScheme.class), new HashMap<String, IssueType>());

        ConfigureResponse result = hook.configure(configureData);

        assertNotNull(result);
    }

    @Test
    public void should_Return_Redirect_Without_Arguments_When_Error_Occurs_During_Project_Configuration() throws NullArgumentException {
        EventOrganizationProjectHook hook = new EventOrganizationProjectHook(mocki18n, mockWorkflowTransitionService);
        ConfigureData configureData = ConfigureData.create(mock(Project.class), mock(Scheme.class), MapUtils.EMPTY_MAP, mock(FieldConfigScheme.class), new HashMap<String, IssueType>());

        ConfigureResponse response = hook.configure(configureData);

        assertEquals(EventOrganizationProjectHook.REDIRECT_URL, response.getRedirect());
    }
}
