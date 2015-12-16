package ut.edu.uz.jira.event.planner.project.configuration;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.I18nHelper;
import com.atlassian.jira.web.HttpServletVariables;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.project.configuration.EventPlanConfigWebworkAction;
import edu.uz.jira.event.planner.project.plan.EventPlanService;
import edu.uz.jira.event.planner.project.plan.model.*;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.project.plan.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.util.text.Internationalization;
import net.java.ao.EntityManager;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.Hsql;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ut.helpers.TestActiveObjects;
import webwork.action.Action;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@Transactional
@RunWith(ActiveObjectsJUnitRunner.class)
@Jdbc(Hsql.class)
@NameConverters
public class EventPlanConfigWebworkActionTest {
    private EntityManager entityManager;
    private TestActiveObjects activeObjects;
    private HttpServletVariables mockHttpVariables;
    private I18nResolver mocki18n;
    private ProjectManager mockProjectManager;
    private VersionManager mockVersionManager;
    private EventPlanService eventPlanService;

    @Before
    public void setUp() throws CreateException {
        mockVersionManager = mock(VersionManager.class);
        mockProjectManager = mock(ProjectManager.class);

        mocki18n = mock(I18nResolver.class);
        Mockito.when(mocki18n.getText(Internationalization.PROJECT_VERSION_NAME)).thenReturn("Event Due Date");
        Mockito.when(mocki18n.getText(Internationalization.PROJECT_VERSION_DESCRIPTION)).thenReturn("Date of an event");

        mockHttpVariables = mock(HttpServletVariables.class);
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);

        JiraAuthenticationContext mockAuthCtx = mock(JiraAuthenticationContext.class);
        I18nHelper mocki18nHelper = mock(I18nHelper.class);
        Mockito.when(mocki18nHelper.getLocale()).thenReturn(Locale.ENGLISH);
        Mockito.when(mockAuthCtx.getI18nHelper()).thenReturn(mocki18nHelper);

        assertNotNull(entityManager);
        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.migrate(Domain.class, Plan.class, Component.class, Plan.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToDomainRelation.class);
        eventPlanService = new EventPlanService(activeObjects);
        eventPlanService.clearDatabase();

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, mock(ComponentAccessor.class))
                .addMock(HttpServletVariables.class, mockHttpVariables)
                .addMock(ProjectManager.class, mockProjectManager)
                .addMock(JiraAuthenticationContext.class, mockAuthCtx)
                .addMock(VersionManager.class, mockVersionManager)
                .init();
    }

    @Test
    public void inputStatusShouldBeReturnedWhenAllRequiredParametersAreEmpty() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("");
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn("ABC");
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mock(Project.class));
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n, eventPlanService);

        String result = fixture.execute();

        assertEquals(Action.INPUT, result);
    }

    @Test
    public void successStatusShouldBeReturnedWhenAllRequiredParametersAreNotEmpty() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("02-12-2015 07:00");
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn("ABC");
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Project mockProject = mock(Project.class);
        List<Version> emptyList = new ArrayList<Version>();
        Mockito.when(mockProject.getVersions()).thenReturn(emptyList);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mockProject);
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n, eventPlanService);

        String result = fixture.execute();

        assertEquals(Action.SUCCESS, result);
    }

    @Test
    public void eventDueDateShouldBeConfiguredAsProjectVersion() throws Exception {
        String projectDueDate = "09-12-2015 23:00";
        String projectKey = "ABC";

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn(projectDueDate);
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn(projectKey);
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        final MockProject mockProject = new MockProject();
        Mockito.when(mockProjectManager.getProjectObjByKey(projectKey)).thenReturn(mockProject);
        Mockito.when(mockVersionManager.createVersion(Mockito.anyString(), Mockito.any(Date.class), Mockito.any(Date.class), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong())).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        Date releaseDate = (Date) invocation.getArguments()[2];

                        Collection<Version> versions = new ArrayList<Version>();
                        Version mockVersion = mock(Version.class);
                        Mockito.when(mockVersion.getReleaseDate()).thenReturn(releaseDate);
                        versions.add(mockVersion);

                        mockProject.setVersions(versions);
                        return mockVersion;
                    }
                });
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n, eventPlanService);

        fixture.execute();

        Collection<Version> versions = mockProject.getVersions();
        Version version = versions.iterator().next();
        DateFormat format = new SimpleDateFormat(EventPlanConfigWebworkAction.DUE_DATE_FORMAT, fixture.getLocale());
        Date releaseDate = format.parse(projectDueDate);
        assertEquals(releaseDate, version.getReleaseDate());
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
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n, eventPlanService);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

    @Test
    public void errorStatusShouldBeReturnedWhenEventTypeIsEmptyAndTheRestAreFilled() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn(null);
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("02-12-2015 23:00");
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn("ABC");
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mock(Project.class));
        Project mockProject = mock(Project.class);
        Mockito.when(mockProject.getVersions()).thenReturn(new ArrayList<Version>());
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mockProject);
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n, eventPlanService);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

    @Test
    public void errorStatusShouldBeReturnedIfProjectNotFound() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("02-12-2015 23:00");
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn(null);
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockProjectManager.getProjectObjByKey(null)).thenReturn(null);
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n, eventPlanService);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

    @Test
    public void errorStatusShouldBeReturnedIfProjectContainsAnyVersion() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("02-12-2015 23:00");
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn("ABC");
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Project mockProject = mock(Project.class);
        Collection<Version> mockVersions = new ArrayList<Version>(1);
        mockVersions.add(mock(Version.class));
        Mockito.when(mockProject.getVersions()).thenReturn(mockVersions);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mockProject);
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n, eventPlanService);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

    @Test
    public void errorStatusShouldBeReturnedIfHttpRequestIsNull() throws Exception {
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(null);
        Project mockProject = mock(Project.class);
        Collection<Version> mockVersions = new ArrayList<Version>(1);
        mockVersions.add(mock(Version.class));
        Mockito.when(mockProject.getVersions()).thenReturn(mockVersions);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mockProject);
        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n, eventPlanService);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

    @Test
    public void shouldReturnEventPlansSortedByDomains() throws Exception {
        String testPlanName = "Test plan 1";
        String secondTestPlanName = "Test plan 2";
        String testDomainName = "Test domain";
        Plan plan1 = createPlanNamed(testPlanName);
        Plan plan2 = createPlanNamed(secondTestPlanName);
        Domain domain = createDomainNamed(testDomainName);
        associate(plan1, domain);
        associate(plan2, domain);

        Map<String, List<String>> expectedResult = new HashMap<>();
        List<String> plans = new ArrayList<>();
        plans.add(testPlanName);
        plans.add(secondTestPlanName);
        expectedResult.put(testDomainName, plans);

        EventPlanConfigWebworkAction fixture = new EventPlanConfigWebworkAction(mocki18n, eventPlanService);

        Map<String, List<String>> result = fixture.getEventPlans();

        assertEquals(expectedResult, result);
    }

    private PlanToDomainRelation associate(Plan plan, Domain domain) {
        PlanToDomainRelation relation = activeObjects.create(PlanToDomainRelation.class);
        relation.setPlan(plan);
        relation.setDomain(domain);
        relation.save();
        return relation;
    }

    private Plan createPlanNamed(String name) {
        Plan result = activeObjects.create(Plan.class);
        result.setName(name);
        result.save();
        return result;
    }

    private Domain createDomainNamed(String name) {
        Domain result = activeObjects.create(Domain.class);
        result.setName(name);
        result.save();
        return result;
    }
}
