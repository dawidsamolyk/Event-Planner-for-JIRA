package ut.edu.uz.jira.event.planner.project.issue.fields;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.layout.field.*;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.ofbiz.MockOfBizDelegator;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.jira.project.Project;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.project.issue.fields.IssueFieldsConfigurator;
import edu.uz.jira.event.planner.utils.Internationalization;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class IssueFieldsConfigurationTest {
    private I18nResolver mocki18n;

    @Before
    public void setUp() {
        FieldLayoutManager  mockFieldLayoutManager = mock(FieldLayoutManager.class);
        EditableDefaultFieldLayout mockDefaultLayout = mock(EditableDefaultFieldLayout.class);
        Mockito.when(mockFieldLayoutManager.getEditableDefaultFieldLayout()).thenReturn(mockDefaultLayout);
        FieldLayoutScheme mockScheme = mock(FieldLayoutScheme.class);
        Mockito.when(mockFieldLayoutManager.createFieldLayoutScheme(Mockito.anyString(),Mockito.anyString())).thenReturn(mockScheme);

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
