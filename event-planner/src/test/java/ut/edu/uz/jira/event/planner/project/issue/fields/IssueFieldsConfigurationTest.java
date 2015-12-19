package ut.edu.uz.jira.event.planner.project.issue.fields;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.layout.field.*;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.ofbiz.MockOfBizDelegator;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.project.Project;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.project.issue.fields.IssueFieldsConfigurator;
import edu.uz.jira.event.planner.util.text.Internationalization;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class IssueFieldsConfigurationTest {
    private I18nResolver mocki18n;

    @Before
    public void setUp() {
        FieldLayoutManager mockFieldLayoutManager = mock(FieldLayoutManager.class);
        EditableDefaultFieldLayout mockDefaultLayout = mock(EditableDefaultFieldLayout.class);
        Mockito.when(mockFieldLayoutManager.getEditableDefaultFieldLayout()).thenReturn(mockDefaultLayout);
        final FieldLayoutScheme mockScheme = mock(FieldLayoutScheme.class);
        final Collection<FieldLayoutSchemeEntity> entities = new ArrayList<FieldLayoutSchemeEntity>();
        Mockito.when(mockScheme.getEntities()).thenReturn(entities);
        Mockito.when(mockFieldLayoutManager.createFieldLayoutScheme(Mockito.anyString(), Mockito.anyString())).thenAnswer(new Answer<FieldLayoutScheme>() {
            @Override
            public FieldLayoutScheme answer(InvocationOnMock invocation) throws Throwable {
                entities.add(mock(FieldLayoutSchemeEntity.class));
                return mockScheme;
            }
        });

        mocki18n = mock(I18nResolver.class);
        Mockito.when(mocki18n.getText(Internationalization.PROJECT_FIELDS_CONFIGURATION_SCHEME_NAME)).thenReturn("Event organization Field Configuration");
        Mockito.when(mocki18n.getText(Internationalization.PROJECT_FIELDS_CONFIGURATION_SCHEME_DESCRIPTION)).thenReturn("Field Configuration for the Event organization Issues");

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, mock(ComponentAccessor.class))
                .addMock(FieldLayoutManager.class, mockFieldLayoutManager)
                .addMock(OfBizDelegator.class, new MockOfBizDelegator())
                .init();
    }

    @Test
    public void should_Create_Instance_Of_Event_Organization_Field_Layout() {
        IssueFieldsConfigurator fixture = new IssueFieldsConfigurator(mocki18n);

        FieldLayout result = fixture.getEventOrganizationFieldLayout();

        assertNotNull(result);
    }

    @Test
    public void should_Create_Instance_Of_Field_Configuration_Scheme() {
        IssueFieldsConfigurator fixture = new IssueFieldsConfigurator(mocki18n);
        Project mockProject = mock(Project.class);
        EditableFieldLayout mockLayout = mock(EditableFieldLayout.class);

        FieldLayoutScheme result = fixture.createFieldConfigurationScheme(mockProject, mockLayout);

        assertNotNull(result);
    }

    @Test
    public void created_Field_Configuration_Scheme_Should_Has() {
        IssueFieldsConfigurator fixture = new IssueFieldsConfigurator(mocki18n);
        Project mockProject = mock(Project.class);
        EditableFieldLayout mockLayout = mock(EditableFieldLayout.class);

        FieldLayoutScheme result = fixture.createFieldConfigurationScheme(mockProject, mockLayout);

        assertNotNull(result);
    }

    @Test
    public void should_Create_Configured_Field_Configuration_Scheme() {
        IssueFieldsConfigurator fixture = new IssueFieldsConfigurator(mocki18n);
        Project mockProject = mock(Project.class);
        Collection<IssueType> issueTypes = new ArrayList<IssueType>();
        issueTypes.add(new MockIssueType("1", "Task"));
        issueTypes.add(new MockIssueType("1", "Sub-Task", true));
        Mockito.when(mockProject.getIssueTypes()).thenReturn(issueTypes);
        EditableFieldLayout mockLayout = mock(EditableFieldLayout.class);

        FieldLayoutScheme result = fixture.createFieldConfigurationScheme(mockProject, mockLayout);

        assertFalse(result.getEntities().isEmpty());
    }
}
