package ut.edu.uz.jira.event.planner.database.importer;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.active.objects.model.*;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToComponentRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.PlanToDomainRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.SubTaskToTaskRelation;
import edu.uz.jira.event.planner.database.active.objects.model.relation.TaskToComponentRelation;
import edu.uz.jira.event.planner.database.importer.xml.EventPlansImporter;
import edu.uz.jira.event.planner.database.importer.xml.model.AllEventPlans;
import edu.uz.jira.event.planner.exception.ActiveObjectSavingException;
import edu.uz.jira.event.planner.exception.EventPlansImportException;
import net.java.ao.EntityManager;
import net.java.ao.test.converters.NameConverters;
import net.java.ao.test.jdbc.Hsql;
import net.java.ao.test.jdbc.Jdbc;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import ut.helpers.TestActiveObjects;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Transactional
@RunWith(ActiveObjectsJUnitRunner.class)
@Jdbc(Hsql.class)
@NameConverters
public class EventPlansImporterTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private EntityManager entityManager;
    private ActiveObjects activeObjects;
    private ActiveObjectsService service;
    private URL testFileUrl;

    @Before
    public void setUp() throws MalformedURLException {
        assertNotNull(entityManager);
        activeObjects = new TestActiveObjects(entityManager);
        activeObjects.migrate(SubTaskToTaskRelation.class, TaskToComponentRelation.class, Domain.class, Plan.class, Component.class, SubTask.class, Task.class, PlanToComponentRelation.class, PlanToDomainRelation.class);
        service = new ActiveObjectsService(activeObjects);
        activeObjects.flushAll();
        service.clearDatabase();

        testFileUrl = new File("src/test/resources/test/test-event-plans.xml").toURI().toURL();
    }

    @Test
    public void should_read_event_plan() throws EventPlansImportException, MalformedURLException {
        EventPlansImporter fixture = new EventPlansImporter(service);

        AllEventPlans result = fixture.getEventPlans(testFileUrl);

        assertNotNull(result.getEventPlan());
    }

    @Test
    public void should_give_error_if_source_file_not_found() throws EventPlansImportException, MalformedURLException {
        EventPlansImporter fixture = new EventPlansImporter(service);

        exception.expect(EventPlansImportException.class);
        fixture.getEventPlans(new File("non-existent-file.xml").toURI().toURL());
    }

    @Test
    public void should_import_event_plans_into_database() throws EventPlansImportException, ActiveObjectSavingException, MalformedURLException {
        EventPlansImporter fixture = new EventPlansImporter(service);
        AllEventPlans plans = fixture.getEventPlans(testFileUrl);

        fixture.importEventPlansIntoDatabase(plans);

        assertEquals(1, activeObjects.count(Plan.class));
        assertEquals(1, activeObjects.count(Domain.class));
        assertEquals(1, activeObjects.count(Component.class));
        assertEquals(2, activeObjects.count(Task.class));
        assertEquals(0, activeObjects.count(SubTask.class));
    }
}
