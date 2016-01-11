package edu.uz.jira.event.planner.database.importer.xml;

import com.atlassian.extras.common.log.Logger;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.importer.xml.model.AllEventPlans;
import edu.uz.jira.event.planner.exception.ActiveObjectSavingException;
import edu.uz.jira.event.planner.exception.EventPlansImportException;
import edu.uz.jira.event.planner.util.text.Internationalization;

import javax.annotation.Nonnull;

/**
 * Automatically imports predefined Event Plans to Active Objects (database) from XML file.
 */
public class EventPlansImportExecutor {
    public static final String APPLICATION_PROPERTY_KEY = "EVENT_PLANS_IMPORTER";
    public static final String IMPORTED = "TRUE";
    public static final String NOT_IMPORTED = "FALSE";
    private final ApplicationProperties applicationProperties = ComponentAccessor.getApplicationProperties();
    private final Logger.Log log = Logger.getInstance(EventPlansImportExecutor.class);
    private final I18nResolver i18nResolver;
    private final ActiveObjectsService activeObjectsService;

    public EventPlansImportExecutor(@Nonnull final I18nResolver i18nResolver,
                                    @Nonnull final ActiveObjectsService activeObjectsService) {
        this.i18nResolver = i18nResolver;
        this.activeObjectsService = activeObjectsService;
    }

    public void startImport() {
        String importedProperty = ComponentAccessor.getApplicationProperties().getText(APPLICATION_PROPERTY_KEY);

        if (importedProperty == null || importedProperty.equals(NOT_IMPORTED)) {
            ImportProcess importProcess = new ImportProcess(activeObjectsService, i18nResolver);
            runImport(importProcess);
        }
    }

    protected void runImport(ImportProcess importProcess) {
        importProcess.run();
    }

    protected void setImported() {
        applicationProperties.setText(APPLICATION_PROPERTY_KEY, IMPORTED);
    }

    protected void setNotImported() {
        applicationProperties.setText(APPLICATION_PROPERTY_KEY, NOT_IMPORTED);
    }

    protected class ImportProcess implements Runnable {
        private final ActiveObjectsService activeObjectsService;
        private final String errorMessage;

        public ImportProcess(final ActiveObjectsService activeObjectsService, I18nResolver i18nResolver) {
            this.activeObjectsService = activeObjectsService;
            this.errorMessage = i18nResolver.getText(Internationalization.EVENT_PLANS_IMPORTER_ERROR);
        }

        @Override
        public void run() {
            try {
                EventPlansImporter importer = new EventPlansImporter(activeObjectsService);
                AllEventPlans predefinedEventPlans = importer.getPredefinedEventPlans();
                importer.importEventPlansIntoDatabase(predefinedEventPlans);

            } catch (EventPlansImportException e) {
                log.error(errorMessage, e);
                setNotImported();
            } catch (ActiveObjectSavingException e) {
                log.error(errorMessage, e);
                setNotImported();
            }
            setImported();
        }
    }
}
