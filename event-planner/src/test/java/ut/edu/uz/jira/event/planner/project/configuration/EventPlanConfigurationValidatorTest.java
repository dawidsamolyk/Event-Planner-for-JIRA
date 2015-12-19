package ut.edu.uz.jira.event.planner.project.configuration;

import com.atlassian.jira.project.MockProject;
import edu.uz.jira.event.planner.project.configuration.EventPlanConfigurationValidator;
import org.apache.commons.collections.ListUtils;
import org.junit.Test;
import ut.edu.uz.jira.event.planner.project.MocksProvider;

import static org.junit.Assert.assertEquals;

public class EventPlanConfigurationValidatorTest {

    @Test
    public void contains_Any_Version_Should_Return_False_For_Null_Project() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();

        boolean result = fixture.containsAnyVersion(null);

        assertEquals(false, result);
    }

    @Test
    public void contains_Any_Version_Should_Return_True_If_Project_Has_Any_Version() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(MocksProvider.getMockVersionsNamed("Some version", "Test version"));

        boolean result = fixture.containsAnyVersion(mockProject);

        assertEquals(true, result);
    }

    @Test
    public void contains_Any_Version_Should_Return_False_If_Project_Has_Not_Any_Version() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.containsAnyVersion(mockProject);

        assertEquals(false, result);
    }

    @Test
    public void configuration_Is_Invalid_If_Project_Is_Null() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();

        boolean result = fixture.isInvalid(null);

        assertEquals(true, result);
    }

    @Test
    public void configuration_Is_Invalid_If_Project_Contains_Any_Version() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(MocksProvider.getMockVersionsNamed("Some version", "Test version"));

        boolean result = fixture.isInvalid(mockProject);

        assertEquals(true, result);
    }

    @Test
    public void can_Input_Project_Configuration_If_All_Requirements_Are_Fulfilled() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.canInputProjectConfiguration(mockProject, "", "");

        assertEquals(true, result);
    }

    @Test
    public void cannot_Input_Project_Configuration_If_Project_Is_Null() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();

        boolean result = fixture.canInputProjectConfiguration(null, "", "");

        assertEquals(false, result);
    }

    @Test
    public void cannot_Input_Project_Configuration_If_Project_Contains_Any_Version() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(MocksProvider.getMockVersionsNamed("Some version", "Test version"));

        boolean result = fixture.canInputProjectConfiguration(mockProject, "", "");

        assertEquals(false, result);
    }

    @Test
    public void cannot_Input_Project_Configuration_If_Event_Type_Is_Not_Empty() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.canInputProjectConfiguration(mockProject, "Undefined", "");

        assertEquals(false, result);
    }

    @Test
    public void cannot_Input_Project_Configuration_If_Event_Due_Date_Is_Filled() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.canInputProjectConfiguration(mockProject, "", "2016-10-10 15:00");

        assertEquals(false, result);
    }

    @Test
    public void can_Configure_Project_Configuration_If_All_Requirements_Are_Fulfilled() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.canConfigureProject(mockProject, "Undefined", "2016-10-10 15:00");

        assertEquals(true, result);
    }

    @Test
    public void cannot_Configure_Project_Configuration_If_Project_Is_Null() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();

        boolean result = fixture.canConfigureProject(null, "Undefined", "2016-10-10 15:00");

        assertEquals(false, result);
    }

    @Test
    public void cannot_Configure_Project_Configuration_If_Project_Contains_Any_Version() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(MocksProvider.getMockVersionsNamed("Some version", "Test version"));

        boolean result = fixture.canConfigureProject(mockProject, "Undefined", "2016-10-10 15:00");

        assertEquals(false, result);
    }

    @Test
    public void cannot_Configure_Project_Configuration_If_Event_Type_Is_Empty() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.canConfigureProject(mockProject, "", "2016-10-10 15:00");

        assertEquals(false, result);
    }

    @Test
    public void cannot_Configure_Project_Configuration_If_Event_Due_Date_Is_Empty() {
        EventPlanConfigurationValidator fixture = new EventPlanConfigurationValidator();
        MockProject mockProject = new MockProject();
        mockProject.setVersions(ListUtils.EMPTY_LIST);

        boolean result = fixture.canConfigureProject(mockProject, "Undefined", "");

        assertEquals(false, result);
    }
}
