package ut.edu.uz.jira.event.planner.database.importer;

import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.importer.xml.EventPlansImportExecutor;
import edu.uz.jira.event.planner.database.importer.xml.EventPlansImporter;
import edu.uz.jira.event.planner.database.importer.xml.model.AllEventPlans;
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
    protected void startImport(@Nonnull EventPlansImportExecutor.ImportProcess importProcess) {
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
                AllEventPlans predefinedEventPlans = importer.getEventPlans(testFileUrl);
                importer.importEventPlansIntoDatabase(predefinedEventPlans);
                setImported();
            } catch (EventPlansImportException e) {
                setNotImported();
            } catch (ActiveObjectSavingException e) {
                setNotImported();
            }
        }
    }
}
