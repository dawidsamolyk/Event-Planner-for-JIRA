package ut.edu.uz.jira.event.planner.database.importer;

import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.xml.importer.EventPlansImportExecutor;
import edu.uz.jira.event.planner.database.xml.importer.EventPlansImportState;
import edu.uz.jira.event.planner.database.xml.importer.EventPlansImporter;
import edu.uz.jira.event.planner.database.xml.model.EventPlanTemplates;
import edu.uz.jira.event.planner.exception.ActiveObjectSavingException;
import edu.uz.jira.event.planner.exception.EventPlansImportException;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class TestingEventPlansImportExecutor extends EventPlansImportExecutor {
    public static ActiveObjectsService activeObjectsService;
    public static File testFile;

    public TestingEventPlansImportExecutor(@Nonnull I18nResolver i18nResolver, @Nonnull ActiveObjectsService activeObjectsService) throws MalformedURLException {
        super(i18nResolver, activeObjectsService);
    }

    @Override
    protected void runImport(EventPlansImportExecutor.ImportProcess importProcess) {
        try {
            new ImportProcess(activeObjectsService).run();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    protected class ImportProcess {
        private final ActiveObjectsService activeObjectsService;
        private final URL testFileUrl;

        public ImportProcess(final ActiveObjectsService activeObjectsService) throws MalformedURLException {
            testFileUrl = testFile.toURI().toURL();
            this.activeObjectsService = activeObjectsService;
        }

        public void run() {
            try {
                EventPlansImporter importer = new EventPlansImporter(activeObjectsService);
                EventPlanTemplates predefinedEventPlans = importer.getEventPlanTemplates(testFileUrl);
                importer.importEventPlansIntoDatabase(predefinedEventPlans);
                setImportState(EventPlansImportState.IMPORTED);
            } catch (EventPlansImportException e) {
                setImportState(EventPlansImportState.NOT_IMPORTED);
            } catch (ActiveObjectSavingException e) {
                setImportState(EventPlansImportState.NOT_IMPORTED);
            }
        }
    }
}
