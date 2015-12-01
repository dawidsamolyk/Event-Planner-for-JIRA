package ut.edu.uz.jira.event.planner.project.issue.fields;

import com.atlassian.jira.component.ComponentAccessor;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class IssueFieldsConfigurationTest {
    private I18nResolver mocki18n = mock(I18nResolver.class);

    @Before
    public void setUp() {
        new MockComponentWorker()
                .addMock(ComponentAccessor.class, mock(ComponentAccessor.class))
                .addMock(FieldLayoutManager.class, mock(FieldLayoutManager.class))
                .init();

        Mockito.when(mocki18n.getText("project.fields.configuration.name")).thenReturn("Event organization Field Configuration");
        Mockito.when(mocki18n.getText("project.fields.configuration.description")).thenReturn("Field Configuration for the Event organization Issues");
    }

    @Test
    public void shouldCreateOnlyOneInstanceOfEventOrganizationFieldLayout() {
        IssueFieldsConfigurator fixture = new IssueFieldsConfigurator(mocki18n);

        FieldLayout first = fixture.getEventOrganizationFieldLayout();
        FieldLayout second = fixture.getEventOrganizationFieldLayout();

        assertTrue(first == second);
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
        FieldLayout mockLayout = mock(FieldLayout.class);

        FieldLayoutScheme result = fixture.createFieldConfigurationScheme(mockProject, mockLayout);

        assertNotNull(result);
    }

    @Test
    public void createdFieldConfigurationSchemeShouldHas() {
        IssueFieldsConfigurator fixture = new IssueFieldsConfigurator(mocki18n);
        Project mockProject = mock(Project.class);
        FieldLayout mockLayout = mock(FieldLayout.class);

        FieldLayoutScheme result = fixture.createFieldConfigurationScheme(mockProject, mockLayout);

        assertNotNull(result);
    }
}
