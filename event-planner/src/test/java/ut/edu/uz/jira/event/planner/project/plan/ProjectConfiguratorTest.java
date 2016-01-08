package ut.edu.uz.jira.event.planner.project.plan;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.JiraException;
import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.SubTaskManager;
import com.atlassian.jira.config.properties.APKeys;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueInputParametersImpl;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.MockApplicationProperties;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.issue.MockIssue;
import com.atlassian.jira.mock.security.MockAuthenticationContext;
import com.atlassian.jira.project.MockProject;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.project.version.VersionManager;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.MockApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.project.plan.ProjectConfigurator;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.TaskToComponentRelation;
import net.java.ao.EntityManager;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.Hsql;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.apache.commons.collections.MapUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import ut.helpers.ActiveObjectsTestHelper;
import ut.helpers.TestActiveObjects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@Transactional
@RunWith(ActiveObjectsJUnitRunner.class)
@Jdbc(Hsql.class)
@NameConverters
public class ProjectConfiguratorTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private EntityManager entityManager;
    private ActiveObjects activeObjects;
    private ActiveObjectsService service;
    private I18nResolver mocki18nResolver;
    private MockProject mockProject;
    private ActiveObjectsTestHelper testHelper;
    private IssueService mockIssueService;


    @Before
    public void setUp() throws Exception {
        assertNotNull(entityManager);
        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.migrate(SubTaskToTaskRelation.class, TaskToComponentRelation.class, Domain.class, Plan.class, Component.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToDomainRelation.class);
        service = new ActiveObjectsService(activeObjects);
        activeObjects.flushAll();
        service.clearDatabase();
        testHelper = new ActiveObjectsTestHelper(activeObjects);

        mocki18nResolver = mock(I18nResolver.class);

        mockProject = new MockProject();
        mockProject.setIssueTypes(new MockIssueType("1", "Task", false, null), new MockIssueType("2", "BubTask", true, null));

        MockProjectManager mockProjectManager = new MockProjectManager();
        mockProjectManager.addProject(mockProject);

        VersionManager mockVersionManager = mock(VersionManager.class);
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

        ProjectComponentManager mockProjectComponentManager = mock(ProjectComponentManager.class);
        Mockito.when(mockProjectComponentManager.create(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong())).thenAnswer(new Answer<ProjectComponent>() {
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

        mockIssueService = mock(IssueService.class);
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

        MockAuthenticationContext mockAuthenticationContext = new MockAuthenticationContext(new MockApplicationUser("test"));
        ApplicationProperties mockAppProperties = new MockApplicationProperties();
        mockAppProperties.setString(APKeys.JIRA_LF_DATE_DMY, "D/MM/yyy");

        SubTaskManager mockSubTaskManager = mock(SubTaskManager.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                MockIssue task = (MockIssue) invocation.getArguments()[0];
                MockIssue subTask = (MockIssue) invocation.getArguments()[1];

                Collection subTasksCollection = new ArrayList();
                subTasksCollection.addAll(task.getSubTaskObjects());
                subTasksCollection.add(subTask);
                task.setSubTaskObjects(subTasksCollection);
                return null;
            }
        }).when(mockSubTaskManager).createSubTaskIssueLink(Mockito.any(Issue.class), Mockito.any(Issue.class), Mockito.any(User.class));

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, mock(ComponentAccessor.class))
                .addMock(JiraAuthenticationContext.class, mockAuthenticationContext)
                .addMock(IssueService.class, mockIssueService)
                .addMock(ProjectManager.class, mockProjectManager)
                .addMock(ProjectComponentManager.class, mockProjectComponentManager)
                .addMock(VersionManager.class, mockVersionManager)
                .addMock(ApplicationProperties.class, mockAppProperties)
                .addMock(SubTaskManager.class, mockSubTaskManager)
                .init();
    }

    @Test
    public void version_should_not_be_created_if_project_is_null() throws ParseException, CreateException {
        ProjectConfigurator fixture = new ProjectConfigurator(mocki18nResolver);

        Version result = fixture.createVersion(null, "09-12-2015 23:00");

        assertNull(result);
    }

    @Test
    public void version_should_not_be_created_if_due_date_is_null() throws ParseException, CreateException {
        ProjectConfigurator fixture = new ProjectConfigurator(mocki18nResolver);

        Version result = fixture.createVersion(mockProject, null);

        assertNull(result);
    }

    @Test
    public void version_should_not_be_created_if_due_date_is_empty_string() throws ParseException, CreateException {
        ProjectConfigurator fixture = new ProjectConfigurator(mocki18nResolver);

        Version result = fixture.createVersion(mockProject, "");

        assertNull(result);
    }

    @Test
    public void version_should_not_be_created_if_due_date_has_incorrect_format() throws ParseException, CreateException {
        ProjectConfigurator fixture = new ProjectConfigurator(mocki18nResolver);

        exception.expect(ParseException.class);
        fixture.createVersion(mockProject, "9/12/2015");
    }

    @Test
    public void version_should_be_created() throws ParseException, CreateException {
        String projectDueDate = "09-12-2015 23:00";
        ProjectConfigurator fixture = new ProjectConfigurator(mocki18nResolver);

        Version result = fixture.createVersion(mockProject, projectDueDate);

        Date expectedDate = new SimpleDateFormat(ProjectConfigurator.DUE_DATE_FORMAT).parse(projectDueDate);
        assertEquals(expectedDate, result.getReleaseDate());
    }

    @Test
    public void should_not_configure_project_if_input_project_is_null() throws JiraException {
        ProjectConfigurator fixture = new ProjectConfigurator(mocki18nResolver);

        List<Issue> result = fixture.configure(null, mock(Version.class), mock(Plan.class));

        assertEquals(0, result.size());
        assertEquals(0, mockProject.getProjectComponents().size());
    }

    @Test
    public void should_not_configure_project_if_version_is_null() throws JiraException {
        ProjectConfigurator fixture = new ProjectConfigurator(mocki18nResolver);

        List<Issue> result = fixture.configure(mockProject, null, mock(Plan.class));

        assertEquals(0, result.size());
        assertEquals(0, mockProject.getProjectComponents().size());
    }

    @Test
    public void should_not_configure_project_if_plan_is_null() throws JiraException {
        ProjectConfigurator fixture = new ProjectConfigurator(mocki18nResolver);

        List<Issue> result = fixture.configure(mockProject, mock(Version.class), null);

        assertEquals(0, result.size());
        assertEquals(0, mockProject.getProjectComponents().size());
    }

    @Test
    public void project_components_should_be_created() throws JiraException {
        Plan plan = testHelper.createPlanNamed("Test plan name");
        Component component = testHelper.createComponentNamed("test 1");
        testHelper.associate(plan, component);
        testHelper.associate(component, testHelper.createTask("test task", 0, 1));
        testHelper.associate(component, testHelper.createTask("test task 2", 0, 2));
        Component secondComponent = testHelper.createComponentNamed("test 2");
        testHelper.associate(secondComponent, testHelper.createTask("test task 3", 0, 3));
        testHelper.associate(plan, secondComponent);
        ProjectConfigurator fixture = new ProjectConfigurator(mocki18nResolver);

        fixture.configure(mockProject, mock(Version.class), plan);

        assertEquals(2, mockProject.getProjectComponents().size());
    }

    @Test
    public void tasks_should_be_created() throws JiraException {
        Plan plan = testHelper.createPlanNamed("Test plan name");
        Component component = testHelper.createComponentNamed("test 1");
        testHelper.associate(plan, component);
        Task firstTask = testHelper.createTask("test task", 0, 1);
        testHelper.associate(component, firstTask);
        Task secondTask = testHelper.createTask("test task 2", 0, 5);
        testHelper.associate(component, secondTask);
        secondTask.save();

        ProjectConfigurator fixture = new ProjectConfigurator(mocki18nResolver);

        List<Issue> result = fixture.configure(mockProject, mock(Version.class), plan);

        assertEquals(2, result.size());
    }

    @Test
    public void sub_tasks_should_be_created() throws JiraException {
        Plan plan = testHelper.createPlanNamed("Test plan name");
        Component component = testHelper.createComponentNamed("test 1");
        testHelper.associate(plan, component);
        Task task = testHelper.createTask("test task", 0, 6);
        testHelper.associate(component, task);
        SubTask firstSubTask = testHelper.createSubTaskNamed("test sub-task");
        testHelper.associate(task, firstSubTask);
        SubTask secondSubTask = testHelper.createSubTaskNamed("test sub-task 2");
        testHelper.associate(task, secondSubTask);

        ProjectConfigurator fixture = new ProjectConfigurator(mocki18nResolver);

        List<Issue> result = fixture.configure(mockProject, mock(Version.class), plan);

        assertEquals(2, result.get(0).getSubTaskObjects().size());
    }

    @Test
    public void should_throw_exception_when_error_occurs_while_creating_task() throws JiraException {
        Plan plan = testHelper.createPlanNamed("Test plan name");
        Component component = testHelper.createComponentNamed("test 1");
        testHelper.associate(plan, component);
        Task task = testHelper.createTask("test task", 0, 8);
        testHelper.associate(component, task);
        task.save();
        Mockito.when(mockIssueService.validateCreate(Mockito.any(ApplicationUser.class), Mockito.any(IssueInputParameters.class))).thenAnswer(new Answer<IssueService.CreateValidationResult>() {
            @Override
            public IssueService.CreateValidationResult answer(InvocationOnMock invocation) throws Throwable {
                IssueService.CreateValidationResult result = mock(IssueService.CreateValidationResult.class);
                Mockito.when(result.isValid()).thenReturn(false);
                Mockito.when(result.getErrorCollection()).thenReturn(mock(ErrorCollection.class));
                return result;
            }
        });

        ProjectConfigurator fixture = new ProjectConfigurator(mocki18nResolver);

        exception.expect(JiraException.class);
        fixture.configure(mockProject, mock(Version.class), plan);
    }

    @Test
    public void should_configure_project_from_complex_event_organization_template() throws JiraException {
        Plan plan = testHelper.createPlanNamed("Complex plan name");
        Component firstComponent = testHelper.createComponentNamed("test 1");
        testHelper.associate(plan, firstComponent);
        Task firstTask = testHelper.createTask("test task", 1, 9);
        testHelper.associate(firstComponent, firstTask);
        SubTask firstSubTask = testHelper.createSubTaskNamed("test sub-task");
        testHelper.associate(firstTask, firstSubTask);
        SubTask secondSubTask = testHelper.createSubTaskNamed("test sub-task 2");
        testHelper.associate(firstTask, secondSubTask);
        Task secondTask = testHelper.createTask("second test task", 0, 2);
        testHelper.associate(firstComponent, secondTask);

        Component secondComponent = testHelper.createComponentNamed("test 2");
        testHelper.associate(plan, secondComponent);
        Task thirdTask = testHelper.createTask("test task 123", 0, 1);
        testHelper.associate(secondComponent, thirdTask);
        SubTask thirdSubTask = testHelper.createSubTaskNamed("test sub-task 1232");
        testHelper.associate(thirdTask, thirdSubTask);
        Task fourthTask = testHelper.createTask("second test task", 0, 3);
        testHelper.associate(secondComponent, fourthTask);

        // This should not be associated because hasn't any Task
        Component thirdComponent = testHelper.createComponentNamed("test 3");
        testHelper.associate(plan, thirdComponent);

        ProjectConfigurator fixture = new ProjectConfigurator(mocki18nResolver);

        List<Issue> result = fixture.configure(mockProject, mock(Version.class), plan);

        assertEquals(4, result.size());
        int subTasksCount = 0;
        for (Issue eachTask : result) {
            subTasksCount = +eachTask.getSubTaskObjects().size();
        }
        assertEquals(3, subTasksCount);
        assertEquals(2, mockProject.getProjectComponents().size());
    }
}
