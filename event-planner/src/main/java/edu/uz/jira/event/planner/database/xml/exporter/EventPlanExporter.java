package edu.uz.jira.event.planner.database.xml.exporter;

import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.xml.importer.ObjectFactory;
import edu.uz.jira.event.planner.database.xml.model.EventPlanTemplates;
import edu.uz.jira.event.planner.database.xml.model.PlanTemplate;

import javax.annotation.Nonnull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;

public class EventPlanExporter {
    private final ActiveObjectsService activeObjectsService;
    private final Marshaller marshaller;

    /**
     * Constructor.
     */
    public EventPlanExporter(@Nonnull final ActiveObjectsService activeObjectsService) throws JAXBException {
        this.activeObjectsService = activeObjectsService;
        this.marshaller = getMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    }

    private Marshaller getMarshaller() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
        return context.createMarshaller();
    }

    public File export(@Nonnull final EventPlanTemplates plans) throws JAXBException, IOException {
        File result = File.createTempFile("jira-plan-template-export", ".xml");
        marshaller.marshal(plans, result);
        return result;
    }
}
