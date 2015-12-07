package ut.edu.uz.jira.event.planner.project;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.mock.MockProjectManager;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.project.ProjectCategoryConfigurator;
import edu.uz.jira.event.planner.utils.InternationalizationKeys;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ProjectCategoryConfiguratorTest {
    private I18nResolver mocki18n;

    @Before
    public void setUp() {
        new MockComponentWorker()
                .addMock(ComponentAccessor.class, Mockito.mock(ComponentAccessor.class))
                .addMock(ProjectManager.class, new MockProjectManager())
                .init();

        mocki18n = mock(I18nResolver.class);
        Mockito.when(mocki18n.getText(InternationalizationKeys.PROJECT_CATEGORY_NAME)).thenReturn("Test name");
        Mockito.when(mocki18n.getText(InternationalizationKeys.PROJECT_CATEGORY_DESCRIPTION)).thenReturn("Test description");
    }

    @Test
    public void projectCategoryShouldBeCreatedOnlyOnce() {
        ProjectCategoryConfigurator fixture = new ProjectCategoryConfigurator(mocki18n);

        ProjectCategory projectCategory = fixture.createProjectCategory();

        assertTrue(fixture.createProjectCategory() == projectCategory);
    }

    @Test
    public void projectCategoryShouldBeCreated() {
        ProjectCategoryConfigurator fixture = new ProjectCategoryConfigurator(mocki18n);

        ProjectCategory projectCategory = fixture.createProjectCategory();

        assertNotNull(projectCategory);
    }
}
