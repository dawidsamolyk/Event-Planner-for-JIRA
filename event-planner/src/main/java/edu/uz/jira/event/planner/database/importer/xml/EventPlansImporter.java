package edu.uz.jira.event.planner.database.importer.xml;

import com.atlassian.core.util.ClassLoaderUtils;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.importer.xml.model.AllEventPlans;
import edu.uz.jira.event.planner.exception.ActiveObjectSavingException;
import edu.uz.jira.event.planner.exception.EventPlansImportException;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.net.URL;

/**
 * Imports into database predefined Event Organization Plans from XML file.
 */
public class EventPlansImporter {
    public static final String PREDEFINED_EVENT_PLANS_RESOURCE_NAME = "/database/predefined-event-plans.xml";
    private final ActiveObjectsService activeObjectsService;
    private final Unmarshaller unmarshaller;

    /**
     * Constructor.
     *
     * @throws EventPlansImportException Thrown when cannot initialize JAXB reader or cannot read data from source file.
     */
    public EventPlansImporter(@Nonnull final ActiveObjectsService activeObjectsService) throws EventPlansImportException {
        this.activeObjectsService = activeObjectsService;
        this.unmarshaller = getUnmarshaller();
    }

    private Unmarshaller getUnmarshaller() throws EventPlansImportException {
        try {
            JAXBContext context = JAXBContext.newInstance(ObjectFactory.class.getPackage().getName());
            return context.createUnmarshaller();
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
    public AllEventPlans getPredefinedEventPlans() throws EventPlansImportException {
        return getEventPlans(getPredefinedEventPlansFileUrl());
    }

    private URL getPredefinedEventPlansFileUrl() {
        return ClassLoaderUtils.getResource(PREDEFINED_EVENT_PLANS_RESOURCE_NAME, getClass());
    }

    /**
     * Reads data from source file.
     *
     * @return Event Plans.
     * @throws EventPlansImportException Thrown when cannot unmarshall or cast data to Event Plans.
     */
    public AllEventPlans getEventPlans(@Nonnull final URL sourceFileUrl) throws EventPlansImportException {
        try {
            return (AllEventPlans) unmarshaller.unmarshal(sourceFileUrl);
        } catch (JAXBException e) {
            throw new EventPlansImportException(e);
        } catch (ClassCastException e) {
            throw new EventPlansImportException(e);
        } catch(IllegalArgumentException e) {
            throw new EventPlansImportException(e);
        }
    }

    /**
     * @param plans Event Plans to import.
     */
    public void importEventPlansIntoDatabase(final AllEventPlans plans) throws ActiveObjectSavingException {
        activeObjectsService.addFrom(plans);
    }
}
