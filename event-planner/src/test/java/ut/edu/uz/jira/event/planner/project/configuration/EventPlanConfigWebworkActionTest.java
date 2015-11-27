package ut.edu.uz.jira.event.planner.project.configuration;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.HttpServletVariables;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.project.configuration.EventPlanConfigWebworkAction;
import org.apache.commons.collections.ListUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import webwork.action.Action;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class EventPlanConfigWebworkActionTest {
    private HttpServletVariables mockHttpVariables = mock(HttpServletVariables.class);
    private I18nResolver mocki18n = mock(I18nResolver.class);
    private ProjectManager mockProjectManager = mock(ProjectManager.class);

    @Before
    public void setUp() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);

        JiraAuthenticationContext mockAuthCtx = mock(JiraAuthenticationContext.class);
        I18nHelper mocki18nHelper = mock(I18nHelper.class);
        Mockito.when(mocki18nHelper.getLocale()).thenReturn(Locale.ENGLISH);
        Mockito.when(mockAuthCtx.getI18nHelper()).thenReturn(mocki18nHelper);

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, mock(ComponentAccessor.class))
                .addMock(HttpServletVariables.class, mockHttpVariables)
                .addMock(ProjectManager.class, mockProjectManager)
                .addMock(JiraAuthenticationContext.class, mockAuthCtx)
                .addMock(VersionManager.class, mock(VersionManager.class))
                .init();

        Mockito.when(mocki18n.getText("project.version.name")).thenReturn("Event Due Date");
        Mockito.when(mocki18n.getText("project.version.description")).thenReturn("Date of an event");
    }

    @Test
    public void inputStatusShouldBeReturnedWhenAllRequiredParametersAreEmpty() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn(null);
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn(null);
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn("ABC");
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mock(Project.class));
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n);

        String result = fixture.execute();

        assertEquals(Action.INPUT, result);
    }


    @Test
    public void successStatusShouldBeReturnedWhenAllRequiredParametersAreNotEmpty() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("2000-01-01");
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn("ABC");
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Project mockProject = mock(Project.class);
        Mockito.when(mockProject.getVersions()).thenReturn(ListUtils.EMPTY_LIST);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mockProject);
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n);

        String result = fixture.execute();

        assertEquals(Action.SUCCESS, result);
    }

    @Test
    public void errorStatusShouldBeReturnedWhenDueDateIsEmptyAndTheRestAreFilled() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn(null);
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn("ABC");
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mock(Project.class));
        Project mockProject = mock(Project.class);
        Mockito.when(mockProject.getVersions()).thenReturn(new ArrayList<Version>());
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mockProject);
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

    @Test
    public void errorStatusShouldBeReturnedWhenEventTypeIsEmptyAndTheRestAreFilled() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn(null);
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("1999-09-09");
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn("ABC");
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mock(Project.class));
        Project mockProject = mock(Project.class);
        Mockito.when(mockProject.getVersions()).thenReturn(new ArrayList<Version>());
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mockProject);
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

    @Test
    public void errorStatusShouldBeReturnedIfProjectNotFound() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("1923-09-09");
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn(null);
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockProjectManager.getProjectObjByKey(null)).thenReturn(null);
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

    @Test
    public void errorStatusShouldBeReturnedIfProjectContainsAnyVersion() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("1923-09-09");
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn("ABC");
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Project mockProject = mock(Project.class);
        Collection<Version> mockVersions = new ArrayList<Version>(1);
        mockVersions.add(mock(Version.class));
        Mockito.when(mockProject.getVersions()).thenReturn(mockVersions);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mockProject);
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

}
