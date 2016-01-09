package ut.edu.uz.jira.event.planner.database.importer;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.mock.MockApplicationProperties;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.TaskToComponentRelation;
import edu.uz.jira.event.planner.database.importer.xml.EventPlansImportExecutor;
import edu.uz.jira.event.planner.exception.EventPlansImportException;
import net.java.ao.EntityManager;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.Hsql;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import ut.helpers.TestActiveObjects;

import java.io.File;
import java.net.MalformedURLException;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@Transactional
@RunWith(ActiveObjectsJUnitRunner.class)
@Jdbc(Hsql.class)
@NameConverters
public class EventPlansImportExecutorTest {
    private I18nResolver mocki18n;
    private EntityManager entityManager;
    private ActiveObjects activeObjects;
    private ActiveObjectsService service;
    private MockApplicationProperties mockApplicationProperties;

    @Before
    public void setUp() {
        mocki18n = mock(I18nResolver.class);

        mockApplicationProperties = new MockApplicationProperties();

        assertNotNull(entityManager);
        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.migrate(SubTaskToTaskRelation.class, TaskToComponentRelation.class, Domain.class, Plan.class, Component.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToDomainRelation.class);
        service = new ActiveObjectsService(activeObjects);
        activeObjects.flushAll();
        service.clearDatabase();

        TestingEventPlansImportExecutor.activeObjectsService = service;
        TestingEventPlansImportExecutor.testFile = new File("src/test/resources/test/test-event-plans.xml");

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, Mockito.mock(ComponentAccessor.class))
                .addMock(ApplicationProperties.class, mockApplicationProperties)
                .init();
    }

    @Test
    public void should_not_import_data_when_it_was_imported_earlier() throws MalformedURLException {
        mockApplicationProperties.setText(EventPlansImportExecutor.APPLICATION_PROPERTY_KEY, EventPlansImportExecutor.IMPORTED);

        new TestingEventPlansImportExecutor(mocki18n, service);

        assertEquals(0, activeObjects.count(Plan.class));
        assertEquals(EventPlansImportExecutor.IMPORTED, mockApplicationProperties.getText(EventPlansImportExecutor.APPLICATION_PROPERTY_KEY));
    }

    @Test
    public void should_import_data_when_it_not_imported() throws MalformedURLException {
        new TestingEventPlansImportExecutor(mocki18n, service);

        assertEquals(1, activeObjects.count(Plan.class));
        assertEquals(EventPlansImportExecutor.IMPORTED, mockApplicationProperties.getText(EventPlansImportExecutor.APPLICATION_PROPERTY_KEY));
    }

    @Test
    public void should_not_import_data_if_source_file_not_found() throws EventPlansImportException, MalformedURLException {
        TestingEventPlansImportExecutor.testFile = new File("non-existent-file");
        new TestingEventPlansImportExecutor(mocki18n, service);

        assertEquals(0, activeObjects.count(Plan.class));
        assertEquals(EventPlansImportExecutor.NOT_IMPORTED, mockApplicationProperties.getText(EventPlansImportExecutor.APPLICATION_PROPERTY_KEY));
    }

    @Test
    public void should_not_import_data_twice() throws MalformedURLException {
        new TestingEventPlansImportExecutor(mocki18n, service);
        assertEquals(EventPlansImportExecutor.IMPORTED, mockApplicationProperties.getText(EventPlansImportExecutor.APPLICATION_PROPERTY_KEY));

        new TestingEventPlansImportExecutor(mocki18n, service);
        assertEquals(1, activeObjects.count(Plan.class));
        assertEquals(EventPlansImportExecutor.IMPORTED, mockApplicationProperties.getText(EventPlansImportExecutor.APPLICATION_PROPERTY_KEY));
    }
}
