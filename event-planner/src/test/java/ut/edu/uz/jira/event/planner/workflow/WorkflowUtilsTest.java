package ut.edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.issue.status.category.StatusCategory;
import com.atlassian.jira.workflow.JiraWorkflow;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.util.WorkflowUtils;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class WorkflowUtilsTest {

    @Test
    public void shouldReturnValidWorkflowStatusesIdsFromSelectedCategory() throws NullArgumentException {
        WorkflowUtils fixture = new WorkflowUtils();
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
        WorkflowUtils fixture = new WorkflowUtils();
        String testCategoryName = "Completed";

        List<String> result = fixture.getStatusesFromCategory(null, testCategoryName);

        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldReturnEmptyStatusesListWhenCategoryNameIsNull() throws NullArgumentException {
        WorkflowUtils fixture = new WorkflowUtils();
        JiraWorkflow mockWorkflow = mock(JiraWorkflow.class);
        List<Status> mockStatuses = new ArrayList<Status>(5);
        mockStatuses.add(getMockStatus("To Do", "1"));
        mockStatuses.add(getMockStatus("In Progress", "892"));
        mockStatuses.add(getMockStatus("In progress", "5"));
        Mockito.when(mockWorkflow.getLinkedStatusObjects()).thenReturn(mockStatuses);

        List<String> result = fixture.getStatusesFromCategory(mockWorkflow, null);

        assertTrue(result.isEmpty());
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
