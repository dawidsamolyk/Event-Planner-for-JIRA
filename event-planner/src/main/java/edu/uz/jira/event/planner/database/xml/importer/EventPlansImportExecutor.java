package edu.uz.jira.event.planner.database.xml.importer;

import com.atlassian.extras.common.log.Logger;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.xml.model.EventPlanTemplates;
import edu.uz.jira.event.planner.exception.ActiveObjectSavingException;
import edu.uz.jira.event.planner.exception.EventPlansImportException;
import edu.uz.jira.event.planner.util.text.Internationalization;

import javax.annotation.Nonnull;

/**
 * Automatically imports predefined Event Plans to Active Objects (database) from XML file.
 */
public class EventPlansImportExecutor {
    private final ApplicationProperties applicationProperties;
    private final Logger.Log log;
    private final I18nResolver i18nResolver;
    private final ActiveObjectsService activeObjectsService;

    /**
     * Constructor.
     *
     * @param i18nResolver         Injected {@code I18nResolver} implementation.
     * @param activeObjectsService Injected {@code ActiveObjectsService} implementation.
     */
    public EventPlansImportExecutor(@Nonnull final I18nResolver i18nResolver,
                                    @Nonnull final ActiveObjectsService activeObjectsService) {
        this.i18nResolver = i18nResolver;
        this.activeObjectsService = activeObjectsService;
        applicationProperties = ComponentAccessor.getApplicationProperties();
        log = Logger.getInstance(EventPlansImportExecutor.class);
    }

    /**
     * Starting import.
     */
    public void startImport() {
        if (!isDataImported()) {
            ImportProcess importProcess = new ImportProcess(activeObjectsService, i18nResolver);
            runImport(importProcess);
        }
    }
    
    public boolean isDataImported() {
        return EventPlansImportState.getApplicationImportState().equals(EventPlansImportState.IMPORTED);
    }

    protected void runImport(ImportProcess importProcess) {
        importProcess.run();
    }

    protected void setImportState(EventPlansImportState state) {
        applicationProperties.setText(EventPlansImportState.APPLICATION_PROPERTY_KEY, state.getState());
    }

    protected class ImportProcess {
        private final ActiveObjectsService activeObjectsService;
        private final String errorMessage;
        private final String successMessage;

        public ImportProcess(final @Nonnull ActiveObjectsService activeObjectsService,
                             final @Nonnull I18nResolver i18nResolver) {
            this.activeObjectsService = activeObjectsService;
            this.errorMessage = i18nResolver.getText(Internationalization.EVENT_PLANS_IMPORTER_ERROR);
            this.successMessage = i18nResolver.getText(Internationalization.EVENT_PLANS_IMPORTER_SUCCESS);
        }

        public void run() {
            try {
                EventPlansImporter importer = new EventPlansImporter(activeObjectsService);
                EventPlanTemplates predefinedEventPlans = importer.getPredefinedEventPlans();
                importer.importEventPlansIntoDatabase(predefinedEventPlans);

                setImportState(EventPlansImportState.IMPORTED);
                log.info(successMessage);
            } catch (EventPlansImportException e) {
                log.error(errorMessage, e);
                setImportState(EventPlansImportState.NOT_IMPORTED);
            } catch (ActiveObjectSavingException e) {
                log.error(errorMessage, e);
                setImportState(EventPlansImportState.NOT_IMPORTED);
            }
        }
    }
}
