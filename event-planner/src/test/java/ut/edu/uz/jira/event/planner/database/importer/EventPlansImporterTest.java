package ut.edu.uz.jira.event.planner.database.importer;

import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.database.importer.xml.EventPlansImporter;
import edu.uz.jira.event.planner.database.importer.xml.model.AllEventPlans;
import edu.uz.jira.event.planner.exception.EventPlansImportException;
import edu.uz.jira.event.planner.util.text.Internationalization;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

public class EventPlansImporterTest {
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private I18nResolver mocki18n;

    @Before
    public void setUp() {
        mocki18n = mock(I18nResolver.class);
        Mockito.when(mocki18n.getText(Internationalization.PREDEFINED_EVENT_PLANS_SOURCE_FILE_PATH)).thenReturn("src/main/resources/database/predefined-event-plans.xml");
    }

    @Test
    public void should_read_event_plan() throws EventPlansImportException {
        EventPlansImporter fixture = new EventPlansImporter(mocki18n);

        AllEventPlans result = fixture.getEventPlans();

        assertNotNull(result.getEventPlan());
    }

    @Test
    public void should_give_error_if_source_file_not_found() throws EventPlansImportException {
        Mockito.when(mocki18n.getText(Internationalization.PREDEFINED_EVENT_PLANS_SOURCE_FILE_PATH)).thenReturn("src/main/resources/database/not-existent-file.xml");
        EventPlansImporter fixture = new EventPlansImporter(mocki18n);

        exception.expect(EventPlansImportException.class);
        fixture.getEventPlans();
    }
}
