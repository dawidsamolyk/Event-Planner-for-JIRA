package ut.edu.uz.jira.event.planner.project.configuration.webwork;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueInputParametersImpl;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.layout.field.MockFieldLayoutManager;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.MockApplicationProperties;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.mock.security.MockAuthenticationContext;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.web.HttpServletVariables;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.project.configuration.webwork.EventPlanConfigurationAction;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.project.plan.ProjectConfigurator;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToCategoryRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.TaskToComponentRelation;
import edu.uz.jira.event.planner.util.text.Internationalization;
import net.java.ao.EntityManager;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.Hsql;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.apache.commons.collections.MapUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ut.helpers.ActiveObjectsTestHelper;
import ut.helpers.TestActiveObjects;
import webwork.action.Action;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;
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
    private ActiveObjectsService activeObjectsService;
    private ActiveObjectsTestHelper testHelper;
    private ProjectComponentManager projectComponentManager;

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

        assertNotNull(entityManager);
        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.migrate(SubTaskToTaskRelation.class, TaskToComponentRelation.class, Category.class, Plan.class, Component.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToCategoryRelation.class);
        activeObjectsService = new ActiveObjectsService(activeObjects);
        activeObjectsService.clearDatabase();

        testHelper = new ActiveObjectsTestHelper(activeObjects);

        projectComponentManager = mock(ProjectComponentManager.class);

        IssueService mockIssueService = mock(IssueService.class);
        Mockito.when(mockIssueService.newIssueInputParameters()).thenReturn(new IssueInputParametersImpl());
        Mockito.when(mockIssueService.validateCreate(Mockito.any(ApplicationUser.class), Mockito.any(IssueInputParameters.class))).thenAnswer(new Answer<IssueService.CreateValidationResult>() {
            @Override
            public IssueService.CreateValidationResult answer(InvocationOnMock invocation) throws Throwable {
                return new IssueService.CreateValidationResult(new MockIssue(), mock(ErrorCollection.class), MapUtils.EMPTY_MAP);
            }
        });
        Mockito.when(mockIssueService.validateSubTaskCreate(Mockito.any(ApplicationUser.class), Mockito.anyLong(), Mockito.any(IssueInputParameters.class))).thenAnswer(new Answer<IssueService.CreateValidationResult>() {
            @Override
            public IssueService.CreateValidationResult answer(InvocationOnMock invocation) throws Throwable {
                return new IssueService.CreateValidationResult(new MockIssue(), mock(ErrorCollection.class), MapUtils.EMPTY_MAP);
            }
        });
        IssueService.IssueResult mockIssueResult = mock(IssueService.IssueResult.class);
        Mockito.when(mockIssueResult.getIssue()).thenReturn(new MockIssue());
        Mockito.when(mockIssueService.create(Mockito.any(ApplicationUser.class), Mockito.any(IssueService.CreateValidationResult.class))).thenReturn(mockIssueResult);

        JiraAuthenticationContext mockAuthCtx = new MockAuthenticationContext(new MockApplicationUser("test"));
        ApplicationProperties mockAppProperties = new MockApplicationProperties();
        mockAppProperties.setString(APKeys.JIRA_LF_DATE_DMY, "D/MM/yyy");

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, mock(ComponentAccessor.class))
                .addMock(HttpServletVariables.class, mockHttpVariables)
                .addMock(ProjectManager.class, mockProjectManager)
                .addMock(JiraAuthenticationContext.class, mockAuthCtx)
                .addMock(VersionManager.class, mockVersionManager)
                .addMock(ProjectComponentManager.class, projectComponentManager)
                .addMock(FieldLayoutManager.class, new MockFieldLayoutManager())
                .addMock(IssueService.class, mockIssueService)
                .addMock(SubTaskManager.class, mock(SubTaskManager.class))
                .addMock(ApplicationProperties.class, mockAppProperties)
                .init();
    }

    @Test
    public void input_Status_Should_Be_Returned_When_All_Required_Parameters_Are_Empty() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("");
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn("ABC");
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mock(Project.class));
        EventPlanConfigurationAction fixture = new EventPlanConfigurationAction(mocki18n, activeObjectsService);

        String result = fixture.execute();

        assertEquals(Action.INPUT, result);
    }

    @Test
    public void success_Status_Should_Be_Returned_When_All_Required_Parameters_Are_Not_Empty() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("02-12-2015 07:00");
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn("ABC");
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Project mockProject = mock(Project.class);
        List<Version> emptyList = new ArrayList<Version>();
        Mockito.when(mockProject.getVersions()).thenReturn(emptyList);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mockProject);
        EventPlanConfigurationAction fixture = new EventPlanConfigurationAction(mocki18n, activeObjectsService);

        String result = fixture.execute();

        assertEquals(Action.SUCCESS, result);
    }

    @Test
    public void event_Due_Date_Should_Be_Configured_As_Project_Version() throws Exception {
        String projectDueDate = "09-12-2015 23:00";
        String projectKey = "ABC";

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn(projectDueDate);
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn(projectKey);
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        final MockProject mockProject = new MockProject();
        mockProject.setIssueTypes(new MockIssueType("1", "Task", false, null), new MockIssueType("2", "BubTask", true, null));
        Mockito.when(mockProjectManager.getProjectObjByKey(projectKey)).thenReturn(mockProject);
        Mockito.when(mockVersionManager.createVersion(Mockito.anyString(), Mockito.any(Date.class), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong())).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        Date releaseDate = (Date) invocation.getArguments()[1];

                        Collection<Version> versions = new ArrayList<Version>();
                        Version mockVersion = mock(Version.class);
                        Mockito.when(mockVersion.getReleaseDate()).thenReturn(releaseDate);
                        versions.add(mockVersion);

                        mockProject.setVersions(versions);
                        return mockVersion;
                    }
                });
        EventPlanConfigurationAction fixture = new EventPlanConfigurationAction(mocki18n, activeObjectsService);

        fixture.execute();

        Collection<Version> versions = mockProject.getVersions();
        Version version = versions.iterator().next();
        DateFormat format = new SimpleDateFormat(ProjectConfigurator.DUE_DATE_FORMAT, fixture.getLocale());
        Date releaseDate = format.parse(projectDueDate);
        assertEquals(releaseDate, version.getReleaseDate());
    }

    @Test
    public void error_Status_Should_Be_Returned_When_Due_Date_Is_Empty_And_The_Rest_Are_Filled() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn(null);
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn("ABC");
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mock(Project.class));
        Project mockProject = mock(Project.class);
        Mockito.when(mockProject.getVersions()).thenReturn(new ArrayList<Version>());
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mockProject);
        EventPlanConfigurationAction fixture = new EventPlanConfigurationAction(mocki18n, activeObjectsService);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

    @Test
    public void error_Status_Should_Be_Returned_When_Event_Type_Is_Empty_And_The_Rest_Are_Filled() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn(null);
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("02-12-2015 23:00");
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn("ABC");
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mock(Project.class));
        Project mockProject = mock(Project.class);
        Mockito.when(mockProject.getVersions()).thenReturn(new ArrayList<Version>());
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mockProject);
        EventPlanConfigurationAction fixture = new EventPlanConfigurationAction(mocki18n, activeObjectsService);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

    @Test
    public void error_Status_Should_Be_Returned_If_Project_Not_Found() throws Exception {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn("02-12-2015 23:00");
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn(null);
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        Mockito.when(mockProjectManager.getProjectObjByKey(null)).thenReturn(null);
        EventPlanConfigurationAction fixture = new EventPlanConfigurationAction(mocki18n, activeObjectsService);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

    @Test
    public void error_Status_Should_Be_Returned_If_Project_Contains_Any_Version() throws Exception {
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
        EventPlanConfigurationAction fixture = new EventPlanConfigurationAction(mocki18n, activeObjectsService);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

    @Test
    public void error_Status_Should_Be_Returned_If_Http_Request_Is_Null() throws Exception {
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(null);
        Project mockProject = mock(Project.class);
        Collection<Version> mockVersions = new ArrayList<Version>(1);
        mockVersions.add(mock(Version.class));
        Mockito.when(mockProject.getVersions()).thenReturn(mockVersions);
        Mockito.when(mockProjectManager.getProjectObjByKey("ABC")).thenReturn(mockProject);
        EventPlanConfigurationAction fixture = new EventPlanConfigurationAction(mocki18n, activeObjectsService);

        String result = fixture.execute();

        assertEquals(Action.ERROR, result);
    }

    @Test
    public void should_Return_Event_Plans_Sorted_By_Domains() throws Exception {
        String testPlanName = "Test plan 1";
        String secondTestPlanName = "Test plan 2";
        String testDomainName = "Test category";
        Plan plan1 = testHelper.createPlanNamed(testPlanName);
        Plan plan2 = testHelper.createPlanNamed(secondTestPlanName);
        Category category = testHelper.createDomainNamed(testDomainName);
        testHelper.associate(plan1, category);
        testHelper.associate(plan2, category);

        Map<String, List<String>> expectedResult = new HashMap<String, List<String>>();
        List<String> plans = new ArrayList<String>();
        plans.add(testPlanName);
        plans.add(secondTestPlanName);
        expectedResult.put(testDomainName, plans);

        EventPlanConfigurationAction fixture = new EventPlanConfigurationAction(mocki18n, activeObjectsService);

        Map<String, List<String>> result = fixture.getEventPlans();

        assertEquals(expectedResult, result);
    }

    @Test
    public void project_elements_should_not_be_created_if_event_organization_plan_not_found() throws Exception {
        String projectDueDate = "09-12-2015 23:00";
        String projectKey = "ABC";
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn("Undefined");
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn(projectDueDate);
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn(projectKey);
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        final MockProject mockProject = new MockProject();
        mockProject.setIssueTypes(new MockIssueType("1", "Task", false, null), new MockIssueType("2", "BubTask", true, null));
        Mockito.when(mockProjectManager.getProjectObjByKey(projectKey)).thenReturn(mockProject);
        Mockito.when(mockVersionManager.createVersion(Mockito.anyString(), Mockito.any(Date.class), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong())).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        Date releaseDate = (Date) invocation.getArguments()[1];

                        Collection<Version> versions = new ArrayList<Version>();
                        Version mockVersion = mock(Version.class);
                        Mockito.when(mockVersion.getReleaseDate()).thenReturn(releaseDate);
                        versions.add(mockVersion);

                        mockProject.setVersions(versions);
                        return mockVersion;
                    }
                });
        EventPlanConfigurationAction fixture = new EventPlanConfigurationAction(mocki18n, activeObjectsService);

        String result = fixture.execute();

        assertEquals(Action.SUCCESS, result);
        assertTrue(mockProject.getComponents().isEmpty());
    }

    @Test
    public void project_components_should_be_created_basing_on_event_organization_plan() throws Exception {
        String projectDueDate = "09-12-2015 23:00";
        String projectKey = "ABC";
        String eventPlanName = "Test event plan";
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getParameter("event-type")).thenReturn(eventPlanName);
        Mockito.when(mockRequest.getParameter("event-duedate")).thenReturn(projectDueDate);
        Mockito.when(mockRequest.getParameter("project-key")).thenReturn(projectKey);
        Mockito.when(mockHttpVariables.getHttpRequest()).thenReturn(mockRequest);
        final MockProject mockProject = new MockProject(12);
        mockProject.setIssueTypes(new MockIssueType("1", "Task", false, null), new MockIssueType("2", "BubTask", true, null));
        Mockito.when(mockProjectManager.getProjectObjByKey(projectKey)).thenReturn(mockProject);
        Mockito.when(mockVersionManager.createVersion(Mockito.anyString(), Mockito.any(Date.class), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong())).thenAnswer(
                new Answer<Object>() {
                    @Override
                    public Object answer(InvocationOnMock invocation) throws Throwable {
                        Date releaseDate = (Date) invocation.getArguments()[1];

                        Collection<Version> versions = new ArrayList<Version>();
                        Version mockVersion = mock(Version.class);
                        Mockito.when(mockVersion.getReleaseDate()).thenReturn(releaseDate);
                        versions.add(mockVersion);

                        mockProject.setVersions(versions);
                        return mockVersion;
                    }
                });
        Mockito.when(projectComponentManager.create(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong())).thenAnswer(new Answer<ProjectComponent>() {
            @Override
            public ProjectComponent answer(InvocationOnMock invocation) throws Throwable {
                ProjectComponent result = mock(ProjectComponent.class);
                Mockito.when(result.getName()).thenReturn((String) invocation.getArguments()[0]);
                Mockito.when(result.getDescription()).thenReturn((String) invocation.getArguments()[1]);
                Mockito.when(result.getLead()).thenReturn((String) invocation.getArguments()[2]);
                Mockito.when(result.getAssigneeType()).thenReturn((Long) invocation.getArguments()[3]);
                Mockito.when(result.getProjectId()).thenReturn((Long) invocation.getArguments()[4]);

                Collection<ProjectComponent> components = new ArrayList<ProjectComponent>();
                components.addAll(mockProject.getProjectComponents());
                components.add(result);
                mockProject.setProjectComponents(components);
                return result;
            }
        });
        Plan plan = testHelper.createPlanNamed(eventPlanName);
        Component firstComponent = testHelper.createComponentNamed("test 1");
        testHelper.associate(firstComponent, testHelper.createTaskNamed("test task 1"));
        testHelper.associate(plan, firstComponent);
        Component secondComponent = testHelper.createComponentNamed("test 2");
        testHelper.associate(secondComponent, testHelper.createTaskNamed("test task 2"));
        testHelper.associate(plan, secondComponent);
        EventPlanConfigurationAction fixture = new EventPlanConfigurationAction(mocki18n, activeObjectsService);

        String result = fixture.execute();

        assertEquals(Action.SUCCESS, result);
        assertEquals(2, mockProject.getProjectComponents().size());
    }
}
