package edu.uz.jira.event.planner.project.plan.rest.manager;

import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.xml.importer.EventPlansImporter;
import edu.uz.jira.event.planner.database.xml.model.EventPlanTemplates;
import edu.uz.jira.event.planner.exception.ActiveObjectSavingException;
import edu.uz.jira.event.planner.exception.EventPlansImportException;
import edu.uz.jira.event.planner.project.plan.rest.RestManagerHelper;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;

/**
 * REST manager which imports Event Plan Template from XML.
 */
@Path("/plan/import")
public class EventPlanTemplateImport {
    private final EventPlansImporter importer;
    private final RestManagerHelper helper;

    /**
     * Constructor.
     *
     * @param activeObjectsService Injected {@code ActiveObjectsService} implementation.
     */
    public EventPlanTemplateImport(@Nonnull final ActiveObjectsService activeObjectsService) throws EventPlansImportException {
        importer = new EventPlansImporter(activeObjectsService);
        helper = new RestManagerHelper();
    }

    /**
     * @param xml XML code of event plan templates.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response put(String xml, @Context final HttpServletRequest request) {
        return importFromXml(xml);
    }

    private Response importFromXml(String xml) {
        StringReader reader = new StringReader(xml);
        EventPlanTemplates predefinedEventPlans;
        try {
            predefinedEventPlans = importer.getEventPlanTemplates(reader);
            importer.importEventPlansIntoDatabase(predefinedEventPlans);
        } catch (EventPlansImportException e) {
            return helper.buildStatus(Response.Status.BAD_REQUEST);
        } catch (ActiveObjectSavingException e) {
            return helper.buildStatus(Response.Status.INTERNAL_SERVER_ERROR);
        } finally {
            reader.close();
        }
        return helper.buildStatus(Response.Status.OK);
    }
}