package ut.edu.uz.jira.event.planner.project.issue.fields;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.Project;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.project.issue.fields.IssueFieldsConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class IssueFieldsConfigurationTest {
    private I18nResolver mocki18n;
    private FieldLayoutManager mockFieldLayoutManager;

    @Before
    public void setUp() {
        mockFieldLayoutManager = mock(FieldLayoutManager.class);

        mocki18n = mock(I18nResolver.class);
        Mockito.when(mocki18n.getText(IssueFieldsConfigurator.PROJECT_FIELDS_CONFIGURATION_SCHEME_NAME_KEY)).thenReturn("Event organization Field Configuration");
        Mockito.when(mocki18n.getText(IssueFieldsConfigurator.PROJECT_FIELDS_CONFIGURATION_SCHEME_DESCRIPTION_KEY)).thenReturn("Field Configuration for the Event organization Issues");

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, mock(ComponentAccessor.class))
                .addMock(FieldLayoutManager.class, mockFieldLayoutManager)
                .init();

    }

    @Test
    public void shouldCreateInstanceOfEventOrganizationFieldLayout() {
        IssueFieldsConfigurator fixture = new IssueFieldsConfigurator(mocki18n);

        FieldLayout result = fixture.getEventOrganizationFieldLayout();

        assertNotNull(result);
    }

    @Test
    public void shouldCreateInstanceOfFieldConfigurationScheme() {
        IssueFieldsConfigurator fixture = new IssueFieldsConfigurator(mocki18n);
        Project mockProject = mock(Project.class);
        EditableFieldLayout mockLayout = mock(EditableFieldLayout.class);

        FieldLayoutScheme result = fixture.createFieldConfigurationScheme(mockProject, mockLayout);

        assertNotNull(result);
    }

    @Test
    public void createdFieldConfigurationSchemeShouldHas() {
        IssueFieldsConfigurator fixture = new IssueFieldsConfigurator(mocki18n);
        Project mockProject = mock(Project.class);
        EditableFieldLayout mockLayout = mock(EditableFieldLayout.class);

        FieldLayoutScheme result = fixture.createFieldConfigurationScheme(mockProject, mockLayout);

        assertNotNull(result);
    }
}
