package ut.edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.JiraException;
import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.i18n.MockI18nHelper;
import com.atlassian.jira.mock.security.MockAuthenticationContext;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.util.SimpleErrorCollection;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.MockJiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import edu.uz.jira.event.planner.workflow.WorkflowConfigurator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.mock;

public class WorkflowConfiguratorTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private WorkflowTransitionService mockWorkflowTransitionService;
    private WorkflowManager mockWorkflowManager;

    @Before
    public void setUp() {
        final JiraWorkflow mockDraft = mock(JiraWorkflow.class);
        Collection<ActionDescriptor> actions = new ArrayList<ActionDescriptor>();
        Mockito.when(mockDraft.getActionsByName(Mockito.anyString())).thenReturn(actions);
        mockWorkflowManager = mock(WorkflowManager.class);
        Mockito.when(mockWorkflowManager.getDraftWorkflow(Mockito.anyString())).thenReturn(mockDraft);

        mockWorkflowTransitionService = mock(WorkflowTransitionService.class);

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, mock(ComponentAccessor.class))
                .addMock(JiraAuthenticationContext.class, new MockAuthenticationContext(new MockApplicationUser("test")))
                .addMock(WorkflowTransitionService.class, mockWorkflowTransitionService)
                .addMock(WorkflowManager.class, mockWorkflowManager)
                .addMock(I18nHelper.class, new MockI18nHelper())
                .init();
    }

    @Test
    public void should_Throw_Exception_If_Any_Error_Occurs_During_Adding_Post_Function_To_Workflow() throws JiraException {
        Mockito.when(mockWorkflowTransitionService.addPostFunctionToWorkflow(Mockito.anyString(), Mockito.any(FunctionDescriptor.class), Mockito.any(JiraWorkflow.class))).thenAnswer(new Answer<ErrorCollection>() {
            @Override
            public ErrorCollection answer(InvocationOnMock invocation) throws Throwable {
                SimpleErrorCollection result = new SimpleErrorCollection();
                result.addError("Test", "Test description");
                return result;
            }
        });
        JiraWorkflow mockWorkflow = new MockJiraWorkflow();
        FunctionDescriptor mockFunction = mock(FunctionDescriptor.class);
        String[] transitionsNames = {"Transition"};
        WorkflowConfigurator fixture = new WorkflowConfigurator(mockWorkflowTransitionService);

        exception.expect(JiraException.class);
        fixture.addToDraft(mockWorkflow, mockFunction, transitionsNames);
    }

    @Test
    public void should_Throw_Exception_If_Any_Error_Occurs_During_Adding_Condition_To_Workflow() throws JiraException {
        Mockito.when(mockWorkflowTransitionService.addConditionToWorkflow(Mockito.anyString(), Mockito.any(ConditionDescriptor.class), Mockito.any(JiraWorkflow.class))).thenAnswer(new Answer<ErrorCollection>() {
            @Override
            public ErrorCollection answer(InvocationOnMock invocation) throws Throwable {
                SimpleErrorCollection result = new SimpleErrorCollection();
                result.addError("Test", "Test description");
                return result;
            }
        });
        JiraWorkflow mockWorkflow = new MockJiraWorkflow();
        ConditionDescriptor mockCondition = mock(ConditionDescriptor.class);
        String[] transitionsNames = {"Transition"};
        WorkflowConfigurator fixture = new WorkflowConfigurator(mockWorkflowTransitionService);

        exception.expect(JiraException.class);
        fixture.addToDraft(mockWorkflow, mockCondition, transitionsNames);
    }

    @Test
    public void should_Throw_Exception_If_Any_Error_Occurs_During_Adding_Validator_To_Workflow() throws JiraException {
        Mockito.when(mockWorkflowManager.getDraftWorkflow(Mockito.anyString())).thenReturn(null);
        JiraWorkflow mockWorkflow = new MockJiraWorkflow();
        ValidatorDescriptor mockvalidator = mock(ValidatorDescriptor.class);
        String[] transitionsNames = {"Transition"};
        WorkflowConfigurator fixture = new WorkflowConfigurator(mockWorkflowTransitionService);

        exception.expect(JiraException.class);
        fixture.addToDraft(mockWorkflow, mockvalidator, transitionsNames);
    }
}
