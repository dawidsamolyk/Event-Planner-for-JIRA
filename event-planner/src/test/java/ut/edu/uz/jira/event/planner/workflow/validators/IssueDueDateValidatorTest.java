package ut.edu.uz.jira.event.planner.workflow.validators;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.version.Version;
import com.atlassian.sal.api.message.I18nResolver;
import com.opensymphony.workflow.InvalidInputException;
import edu.uz.jira.event.planner.util.text.Internationalization;
import edu.uz.jira.event.planner.workflow.validators.IssueDueDateValidator;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.*;

import static org.mockito.Mockito.mock;

public class IssueDueDateValidatorTest {
    public static final String TEST_PROJECT_VERSION_NAME = "Test";
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private I18nResolver mocki18n;

    @Before
    public void setUp() {
        mocki18n = mock(I18nResolver.class);
        Mockito.when(mocki18n.getText(Internationalization.PROJECT_VERSION_NAME)).thenReturn(TEST_PROJECT_VERSION_NAME);
    }

    @Test
    public void should_Throw_Exception_When_Issue_Is_Null() throws InvalidInputException {
        IssueDueDateValidator fixture = new IssueDueDateValidator(mocki18n);
        Map transientVars = new HashMap();
        transientVars.put("issue", null);

        exception.expect(InvalidInputException.class);
        fixture.validate(transientVars, null, null);
    }

    @Test
    public void should_Throw_Exception_When_Issue_Due_Date_Is_After_Project_Due_Date() throws InvalidInputException {
        Long time = new Date().getTime();
        Project mockProject = getMockProjectWithVersionDueDate(time);
        Issue mockIssue = getMockIssueWithDueDate(mockProject, (time + 10000));
        IssueDueDateValidator fixture = new IssueDueDateValidator(mocki18n);
        Map transientVars = new HashMap();
        transientVars.put("issue", mockIssue);

        exception.expect(InvalidInputException.class);
        fixture.validate(transientVars, null, null);
    }

    @Test
    public void should_Throw_Exception_When_Issue_Due_Date_Is_Empty() throws InvalidInputException {
        Long time = new Date().getTime();
        Project mockProject = getMockProjectWithVersionDueDate(time);
        Issue mockIssue = getMockIssueWithDueDate(mockProject, null);
        IssueDueDateValidator fixture = new IssueDueDateValidator(mocki18n);
        Map transientVars = new HashMap();
        transientVars.put("issue", mockIssue);

        exception.expect(InvalidInputException.class);
        fixture.validate(transientVars, null, null);
    }

    @Test
    public void should_Pass_When_Issue_Due_Date_Is_Before_Project_Due_Date() throws InvalidInputException {
        Long time = new Date().getTime();
        Project mockProject = getMockProjectWithVersionDueDate(time);
        Issue mockIssue = getMockIssueWithDueDate(mockProject, (time - 10000));
        IssueDueDateValidator fixture = new IssueDueDateValidator(mocki18n);
        Map transientVars = new HashMap();
        transientVars.put("issue", mockIssue);

        fixture.validate(transientVars, null, null);
    }

    @Test
    public void should_Pass_When_Issue_Due_Date_Is_In_The_Same_Moment_With_Project_Due_Date() throws InvalidInputException {
        Long time = new Date().getTime();
        Project mockProject = getMockProjectWithVersionDueDate(time);
        Issue mockIssue = getMockIssueWithDueDate(mockProject, (time));
        IssueDueDateValidator fixture = new IssueDueDateValidator(mocki18n);
        Map transientVars = new HashMap();
        transientVars.put("issue", mockIssue);

        fixture.validate(transientVars, null, null);
    }

    private Issue getMockIssueWithDueDate(Project mockProject, Long time) {
        Issue mockIssue = mock(Issue.class);
        Mockito.when(mockIssue.getProjectObject()).thenReturn(mockProject);
        if (time != null) {
            Mockito.when(mockIssue.getDueDate()).thenReturn(new Timestamp(time));
        } else {
            Mockito.when(mockIssue.getDueDate()).thenReturn(null);
        }
        return mockIssue;
    }

    private Project getMockProjectWithVersionDueDate(Long time) {
        Version mockVersion = mock(Version.class);
        if (time != null) {
            Mockito.when(mockVersion.getReleaseDate()).thenReturn(new Date(time));
        } else {
            Mockito.when(mockVersion.getReleaseDate()).thenReturn(null);
        }
        Mockito.when(mockVersion.getName()).thenReturn(TEST_PROJECT_VERSION_NAME);

        Project mockProject = mock(Project.class);
        Collection<Version> mockVersions = new ArrayList<Version>();
        mockVersions.add(mockVersion);
        Mockito.when(mockProject.getVersions()).thenReturn(mockVersions);
        return mockProject;
    }
}
