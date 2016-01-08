package edu.uz.jira.event.planner.database.importer.xml;

import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.database.importer.xml.model.AllEventPlans;
import edu.uz.jira.event.planner.exception.EventPlansImportException;
import edu.uz.jira.event.planner.util.text.Internationalization;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Imports into database predefined Event Organization Plans from XML file.
 */
public class EventPlansImporter {
    private final File sourceFile;
    private final Unmarshaller unmarshaller;

    /**
     * Constructor.
     *
     * @param i18nResolver Injected {@code I18nResolver} implementation.
     * @throws EventPlansImportException Thrown when cannot initialize JAXB reader or cannot read data from source file.
     */
    public EventPlansImporter(@Nonnull final I18nResolver i18nResolver) throws EventPlansImportException {
        String sourceFilePath = i18nResolver.getText(Internationalization.PREDEFINED_EVENT_PLANS_SOURCE_FILE_PATH);
        sourceFile = new File(sourceFilePath);

        try {
            JAXBContext context = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new EventPlansImportException(e);
        }
    }

    /**
     * Reads data from source file.
     *
     * @return Event Plans.
     * @throws EventPlansImportException Thrown when cannot unmarshall or cast data to Event Plans.
     */
    public AllEventPlans getEventPlans() throws EventPlansImportException {
        try {
            return (AllEventPlans) unmarshaller.unmarshal(sourceFile);
        } catch (JAXBException e) {
            throw new EventPlansImportException(e);
        } catch (ClassCastException e) {
            throw new EventPlansImportException(e);
        }
    }
}
