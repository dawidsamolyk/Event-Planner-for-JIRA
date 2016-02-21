package edu.uz.jira.event.planner.database.xml.exporter;

import edu.uz.jira.event.planner.database.xml.importer.ObjectFactory;
import edu.uz.jira.event.planner.database.xml.model.EventPlanTemplates;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;

public class EventPlanExporter {
    private final Marshaller marshaller;
    private final String temporaryFilePrefix = "jira-plan-template-export";
    private final String temporaryFileExtension = ".xml";

    /**
     * Constructor.
     */
    public EventPlanExporter() throws JAXBException {
        this.marshaller = getMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    }

    private Marshaller getMarshaller() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        return context.createMarshaller();
    }

    public File export(@Nonnull final EventPlanTemplates plans) throws JAXBException, IOException {
        File result = File.createTempFile(temporaryFilePrefix, temporaryFileExtension);
        marshaller.marshal(plans, result);
        return result;
    }
}
