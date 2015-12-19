package ut.edu.uz.jira.event.planner.project;

import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.util.ProjectUtils;
import edu.uz.jira.event.planner.util.text.Internationalization;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class ProjectUtilsTest {
    public static final String TEST_VERSION_NAME = "Test";
    @Rule
    public ExpectedException exception = ExpectedException.none();

    private I18nResolver mocki18n;

    @Before
    public void setUp() {
        mocki18n = mock(I18nResolver.class);
        Mockito.when(mocki18n.getText(Internationalization.PROJECT_VERSION_NAME)).thenReturn(TEST_VERSION_NAME);
    }

    @Test
    public void should_Throw_Error_When_Trying_To_Get_Due_Date_Verion_Of_Null_Project() throws NullArgumentException {
        ProjectUtils fixture = new ProjectUtils(mocki18n);

        exception.expect(NullArgumentException.class);
        fixture.getDueDateVersion(null);
    }

    @Test
    public void should_Return_Null_If_No_Due_Date_Version_Found_In_Project() throws NullArgumentException {
        ProjectUtils fixture = new ProjectUtils(mocki18n);
        Project mockProject = new MockProject();

        Version version = fixture.getDueDateVersion(mockProject);

        assertNull(version);
    }

    @Test
    public void should_Return_Null_If_Due_Date_Version_Not_Found_In_Project_Versions() throws NullArgumentException {
        ProjectUtils fixture = new ProjectUtils(mocki18n);
        MockProject mockProject = new MockProject();
        mockProject.setVersions(MocksProvider.getMockVersionsNamed("Some version"));

        Version version = fixture.getDueDateVersion(mockProject);

        assertNull(version);
    }

    @Test
    public void should_Return_Due_Date_Version_From_Specified_Project() throws NullArgumentException {
        ProjectUtils fixture = new ProjectUtils(mocki18n);
        MockProject mockProject = new MockProject();
        mockProject.setVersions(MocksProvider.getMockVersionsNamed(TEST_VERSION_NAME, "Another version"));

        Version version = fixture.getDueDateVersion(mockProject);

        assertNotNull(version);
    }


}
