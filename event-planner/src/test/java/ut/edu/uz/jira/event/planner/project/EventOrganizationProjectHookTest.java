package ut.edu.uz.jira.event.planner.project;

import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.blueprint.api.ConfigureData;
import com.atlassian.jira.blueprint.api.ConfigureResponse;
import com.atlassian.jira.blueprint.api.ValidateData;
import com.atlassian.jira.blueprint.api.ValidateResponse;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.plugins.workflow.sharing.importer.JiraWorkflowSharingImporter;
import com.atlassian.jira.plugins.workflow.sharing.importer.SharedWorkflowImportPlan;
import com.atlassian.jira.plugins.workflow.sharing.importer.component.WorkflowImporterFactoryImpl;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.scheme.Scheme;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import edu.uz.jira.event.planner.project.EventOrganizationProjectHook;
import edu.uz.jira.event.planner.workflow.UpdateDueDatePostFunction;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import sun.awt.AWTAccessor;

import java.util.Collection;
import java.util.HashMap;

import static javafx.beans.binding.Bindings.when;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

/**
 * Created by Dawid on 2015-11-03.
 */
public class EventOrganizationProjectHookTest {
    private WorkflowTransitionService mockWorkflowTransitionService;

    @Before
    public void setUp() {
        new MockComponentWorker()
                .addMock(ComponentAccessor.class, Mockito.mock(ComponentAccessor.class))
                .addMock(FieldLayoutManager.class, Mockito.mock(FieldLayoutManager.class))
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

//    @Test
//    public void eventOrganizationWorkflowShouldHasAddedUpdateDueDatePostFunction() {
//        EventOrganizationProjectHook hook = new EventOrganizationProjectHook(mockWorkflowTransitionService);
//
//        HashMap<String, JiraWorkflow> createdWorkflows = new HashMap<String, JiraWorkflow>();
//        ConfigureData configureData = ConfigureData.create(mock(Project.class), mock(Scheme.class), createdWorkflows, mock(FieldConfigScheme.class), new HashMap<String, IssueType>());
//
//        hook.configure(configureData);
//
//        int foundMatchingPostFunctions = 0;
//        JiraWorkflow eventOrganizationWorkflow = configureData.createdWorkflows().get("EVENT-ORGANIZATION-WORKFLOW");
//        for (ActionDescriptor eachAction : eventOrganizationWorkflow.getActionsByName("Deadline exceeded")) {
//            Collection<FunctionDescriptor> postFunctions = eventOrganizationWorkflow.getPostFunctionsForTransition(eachAction);
//            for (FunctionDescriptor eachDescriptor : postFunctions) {
//                if (eachDescriptor.getArgs().get("class.name").equals(UpdateDueDatePostFunction.class.getName())) {
//                    foundMatchingPostFunctions++;
//                }
//            }
//        }
//        assertEquals(2, foundMatchingPostFunctions);
//    }
}
