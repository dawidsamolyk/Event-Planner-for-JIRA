package ut.edu.uz.jira.event.planner.project.configuration;

import com.atlassian.jira.project.MockProject;
import edu.uz.jira.event.planner.project.configuration.EventPlanConfigurationValidator;
import org.apache.commons.collections.ListUtils;
import org.junit.Test;
import ut.edu.uz.jira.event.planner.project.MocksProvider;

import static org.junit.Assert.assertEquals;

public class EventPlanConfigurationValidatorTest {

    @Test
    public void containsAnyVersionShouldReturnFalseForNullProject() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();

        boolean result = fixture.containsAnyVersion(null);

        assertEquals(false, result);
    }

    @Test
    public void containsAnyVersionShouldReturnTrueIfProjectHasAnyVersion() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(MocksProvider.getMockVersionsNamed("Some version", "Test version"));

        boolean result = fixture.containsAnyVersion(mockProject);

        assertEquals(true, result);
    }

    @Test
    public void containsAnyVersionShouldReturnFalseIfProjectHasNotAnyVersion() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.containsAnyVersion(mockProject);

        assertEquals(false, result);
    }

    @Test
    public void configurationIsInvalidIfProjectIsNull() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();

        boolean result = fixture.isInvalid(null);

        assertEquals(true, result);
    }

    @Test
    public void configurationIsInvalidIfProjectContainsAnyVersion() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(MocksProvider.getMockVersionsNamed("Some version", "Test version"));

        boolean result = fixture.isInvalid(mockProject);

        assertEquals(true, result);
    }

    @Test
    public void canInputProjectConfigurationIfAllRequirementsAreFulfilled() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.canInputProjectConfiguration(mockProject, "", "");

        assertEquals(true, result);
    }

    @Test
    public void cannotInputProjectConfigurationIfProjectIsNull() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();

        boolean result = fixture.canInputProjectConfiguration(null, "", "");

        assertEquals(false, result);
    }

    @Test
    public void cannotInputProjectConfigurationIfProjectContainsAnyVersion() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(MocksProvider.getMockVersionsNamed("Some version", "Test version"));

        boolean result = fixture.canInputProjectConfiguration(mockProject, "", "");

        assertEquals(false, result);
    }

    @Test
    public void cannotInputProjectConfigurationIfEventTypeIsNotEmpty() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.canInputProjectConfiguration(mockProject, "Undefined", "");

        assertEquals(false, result);
    }

    @Test
    public void cannotInputProjectConfigurationIfEventDueDateIsFilled() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.canInputProjectConfiguration(mockProject, "", "2016-10-10 15:00");

        assertEquals(false, result);
    }

    //----------------------
    @Test
    public void canConfigureProjectConfigurationIfAllRequirementsAreFulfilled() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.canConfigureProject(mockProject, "Undefined", "2016-10-10 15:00");

        assertEquals(true, result);
    }

    @Test
    public void cannotConfigureProjectConfigurationIfProjectIsNull() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();

        boolean result = fixture.canConfigureProject(null, "Undefined", "2016-10-10 15:00");

        assertEquals(false, result);
    }

    @Test
    public void cannotConfigureProjectConfigurationIfProjectContainsAnyVersion() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(MocksProvider.getMockVersionsNamed("Some version", "Test version"));

        boolean result = fixture.canConfigureProject(mockProject, "Undefined", "2016-10-10 15:00");

        assertEquals(false, result);
    }

    @Test
    public void cannotConfigureProjectConfigurationIfEventTypeIsEmpty() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.canConfigureProject(mockProject, "", "2016-10-10 15:00");

        assertEquals(false, result);
    }

    @Test
    public void cannotConfigureProjectConfigurationIfEventDueDateIsEmpty() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.canConfigureProject(mockProject, "Undefined", "");

        assertEquals(false, result);
    }
}
