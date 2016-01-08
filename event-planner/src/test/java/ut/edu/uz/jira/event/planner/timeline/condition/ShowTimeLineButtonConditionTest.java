package ut.edu.uz.jira.event.planner.timeline.condition;

import com.atlassian.jira.mock.project.MockVersion;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.jira.project.version.Version;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.timeline.condition.ShowTimeLineButtonCondition;
import edu.uz.jira.event.planner.util.text.Internationalization;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ShowTimeLineButtonConditionTest {
    private static final String PROJECT_CATEGORY_NAME = "Event Organization Plan";
    private static final String PROJECT_VERSION_NAME = "Event Deadline";
    private I18nResolver mocki18nResolver;

    @Before
    public void setUp() {
        mocki18nResolver = mock(I18nResolver.class);
        Mockito.when(mocki18nResolver.getText(Internationalization.PROJECT_CATEGORY_NAME)).thenReturn(PROJECT_CATEGORY_NAME);
        Mockito.when(mocki18nResolver.getText(Internationalization.PROJECT_VERSION_NAME)).thenReturn(PROJECT_VERSION_NAME);
    }

    @Test
    public void if_no_project_in_context_should_not_display_button() {
        ShowTimeLineButtonCondition fixture = new ShowTimeLineButtonCondition(mocki18nResolver);
        Map<String, Object> context = new HashMap<String, Object>();

        boolean result = fixture.shouldDisplay(context);

        assertEquals(false, result);
    }

    @Test
    public void if_invalid_project_category_then_should_not_display_button() {
        ShowTimeLineButtonCondition fixture = new ShowTimeLineButtonCondition(mocki18nResolver);
        ProjectCategory mockProjectCategory = mock(ProjectCategory.class);
        Mockito.when(mockProjectCategory.getName()).thenReturn("Test project category");
        MockProject mockProject = new MockProject();
        mockProject.setProjectCategory(mockProjectCategory);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(ShowTimeLineButtonCondition.PROJECT_KEY, mockProject);

        boolean result = fixture.shouldDisplay(context);

        assertEquals(false, result);
    }

    @Test
    public void if_project_has_not_any_version_then_should_not_display_button() {
        ShowTimeLineButtonCondition fixture = new ShowTimeLineButtonCondition(mocki18nResolver);
        ProjectCategory mockProjectCategory = mock(ProjectCategory.class);
        Mockito.when(mockProjectCategory.getName()).thenReturn(PROJECT_CATEGORY_NAME);
        MockProject mockProject = new MockProject();
        mockProject.setProjectCategory(mockProjectCategory);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(ShowTimeLineButtonCondition.PROJECT_KEY, mockProject);

        boolean result = fixture.shouldDisplay(context);

        assertEquals(false, result);
    }

    @Test
    public void if_project_has_not_deadline_version_then_should_not_display_button() {
        ShowTimeLineButtonCondition fixture = new ShowTimeLineButtonCondition(mocki18nResolver);
        ProjectCategory mockProjectCategory = mock(ProjectCategory.class);
        Mockito.when(mockProjectCategory.getName()).thenReturn(PROJECT_CATEGORY_NAME);
        MockProject mockProject = new MockProject();
        mockProject.setVersions(Arrays.asList(new Version[]{new MockVersion(123l, "Test version name")}));
        mockProject.setProjectCategory(mockProjectCategory);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(ShowTimeLineButtonCondition.PROJECT_KEY, mockProject);

        boolean result = fixture.shouldDisplay(context);

        assertEquals(false, result);
    }

    @Test
    public void if_project_is_event_organization_then_should_display_button() {
        ShowTimeLineButtonCondition fixture = new ShowTimeLineButtonCondition(mocki18nResolver);
        ProjectCategory mockProjectCategory = mock(ProjectCategory.class);
        Mockito.when(mockProjectCategory.getName()).thenReturn(PROJECT_CATEGORY_NAME);
        MockProject mockProject = new MockProject();
        mockProject.setVersions(Arrays.asList(new Version[]{new MockVersion(123l, PROJECT_VERSION_NAME)}));
        mockProject.setProjectCategory(mockProjectCategory);
        Map<String, Object> context = new HashMap<String, Object>();
        context.put(ShowTimeLineButtonCondition.PROJECT_KEY, mockProject);

        boolean result = fixture.shouldDisplay(context);

        assertEquals(true, result);
    }

}
