package ut.edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.issue.status.category.StatusCategory;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import edu.uz.jira.event.planner.exceptions.NullArgumentException;
import edu.uz.jira.event.planner.workflow.WorkflowConfigurator;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class WorkflowConfiguratorTest {

    @Test
    public void shouldReturnValidWorkflowStatusesIdsFromSelectedCategory() throws NullArgumentException {
        WorkflowConfigurator fixture = new WorkflowConfigurator();
        String testCategoryName = "Completed";
        List<String> expectedResult = new ArrayList<String>(2);
        expectedResult.add("1209");
        expectedResult.add("90");
        JiraWorkflow mockWorkflow = mock(JiraWorkflow.class);
        List<Status> mockStatuses = new ArrayList<Status>(5);
        mockStatuses.add(getMockStatus("To Do", "1"));
        mockStatuses.add(getMockStatus("In Progress", "892"));
        mockStatuses.add(getMockStatus(testCategoryName, "1209"));
        mockStatuses.add(getMockStatus(testCategoryName, "90"));
        mockStatuses.add(getMockStatus("In progress", "5"));
        Mockito.when(mockWorkflow.getLinkedStatusObjects()).thenReturn(mockStatuses);

        List<String> result = fixture.getStatusesFromCategory(mockWorkflow, testCategoryName);

        assertArrayEquals(expectedResult.toArray(), result.toArray());
    }

    @Test
    public void shouldReturnEmptyStatusesListWhenWorkflowIsNull() throws NullArgumentException {
        WorkflowConfigurator fixture = new WorkflowConfigurator();
        String testCategoryName = "Completed";

        List<String> result = fixture.getStatusesFromCategory(null, testCategoryName);

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnEmptyStatusesListWhenCategoryNameIsNull() throws NullArgumentException {
        WorkflowConfigurator fixture = new WorkflowConfigurator();
        JiraWorkflow mockWorkflow = mock(JiraWorkflow.class);
        List<Status> mockStatuses = new ArrayList<Status>(5);
        mockStatuses.add(getMockStatus("To Do", "1"));
        mockStatuses.add(getMockStatus("In Progress", "892"));
        mockStatuses.add(getMockStatus("In progress", "5"));
        Mockito.when(mockWorkflow.getLinkedStatusObjects()).thenReturn(mockStatuses);

        List<String> result = fixture.getStatusesFromCategory(mockWorkflow, null);

        assertTrue(result.isEmpty());
    }

    @Test
    public void subTaskBlockingConditionDescriptorShouldContainsStatusesSeparatedByComma() {
        String expectedResult = "1203,90,123";
        List<String> statusesToBlock = new ArrayList<String>(3);
        statusesToBlock.add("1203");
        statusesToBlock.add("90");
        statusesToBlock.add("123");

        ConditionDescriptor result = WorkflowConfigurator.createSubTaskBlockingConditionDescriptor(statusesToBlock);

        assertEquals(expectedResult, result.getArgs().get("statuses"));
    }

    @Test
    public void subTaskBlockingConditionDescriptorNeverShouldBeNull() {
        ConditionDescriptor result = WorkflowConfigurator.createSubTaskBlockingConditionDescriptor(null);

        assertNotNull(result);
    }

    private Status getMockStatus(String categoryName, String id) {
        StatusCategory mockCategory = mock(StatusCategory.class);
        Mockito.when(mockCategory.getName()).thenReturn(categoryName);

        Status result = mock(Status.class);
        Mockito.when(result.getStatusCategory()).thenReturn(mockCategory);
        Mockito.when(result.getId()).thenReturn(id);

        return result;
    }
}
