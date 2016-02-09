package edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.xml.converter.ProjectToTemplateConverter;
import edu.uz.jira.event.planner.database.xml.exporter.EventPlanExporter;
import edu.uz.jira.event.planner.database.xml.model.EventPlanTemplates;
import edu.uz.jira.event.planner.database.xml.model.PlanTemplate;
import edu.uz.jira.event.planner.exception.ActiveObjectSavingException;
import edu.uz.jira.event.planner.exception.EmptyComponentsListException;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.project.plan.rest.ActiveObjectWrapper;
import edu.uz.jira.event.planner.util.text.TextUtils;
import net.java.ao.Entity;
import net.java.ao.Query;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * REST manager for Event Organization Plans.
 */
@Path("/plan")
public class EventPlanRestManager extends RestManager {
    private final ProjectManager projectManager;
    private final ProjectToTemplateConverter converter;


    /**
     * Constructor.
     *
     * @param userManager          Injected {@code UserManager} implementation.
     * @param transactionTemplate  Injected {@code TransactionTemplate} implementation.
     * @param activeObjectsService Event Organization Plan Service which manages Active Objects (Plans, Domains, Tasks etc.).
     * @param i18nResolver          Injected {@code I18nResolver} implementation.
     */
    public EventPlanRestManager(@Nonnull final UserManager userManager,
                                @Nonnull final TransactionTemplate transactionTemplate,
                                @Nonnull final ActiveObjectsService activeObjectsService,
                                @Nonnull final I18nResolver i18nResolver) {
        super(userManager, transactionTemplate, activeObjectsService, PlanTemplate.createEmpty());
        projectManager = ComponentAccessor.getProjectManager();
        converter = new ProjectToTemplateConverter(i18nResolver);
    }

    /**
     * Exports selected Event Plan to XML file. Returns File instance.
     *
     * @param planId  ID of plan to export.
     * @param request Http Servlet request.
     * @return Response of request.
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public Response post(String planId, @Context final HttpServletRequest request) {
        final int id;
        try {
            id = Integer.parseInt(planId);
        } catch (NumberFormatException e) {
            return helper.buildStatus(Response.Status.NOT_ACCEPTABLE);
        }

        final ActiveObjectWrapper[] foundPlans = getEntities(entityType, Query.select().where("ID = ?", id));

        if (foundPlans.length == 0) {
            return helper.buildStatus(Response.Status.NOT_FOUND);
        }

        EventPlanTemplates toExport = new EventPlanTemplates();
        for (ActiveObjectWrapper each : foundPlans) {
            if (each instanceof PlanTemplate) {
                toExport.addPlan((PlanTemplate) each);
            }
        }

        try {
            final EventPlanExporter exporter = new EventPlanExporter(activeObjectsService);
            final File result = exporter.export(toExport);

            return Response.ok(transactionTemplate.execute(new TransactionCallback<File>() {
                public File doInTransaction() {
                    return result;
                }
            })).build();

        } catch (JAXBException e) {
            return helper.buildStatus(Response.Status.CONFLICT);
        } catch (IOException e) {
            return helper.buildStatus(Response.Status.CONFLICT);
        }
    }

    /**
     * Adds new Event Plan template basing on existent Project.
     *
     * @param configuration Configuration of new Event Plan template.
     * @param request       Http Servlet request.
     * @return Response of request.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(NewPlanTemplateConfiguration configuration, @Context final HttpServletRequest request) {
        if (configuration == null || !configuration.isFullfilled()) {
            return helper.buildStatus(Response.Status.PRECONDITION_FAILED);
        }

        Project project = projectManager.getProjectByCurrentKeyIgnoreCase(configuration.getProjectKey());
        if (project == null) {
            return helper.buildStatus(Response.Status.NOT_FOUND);
        }

        PlanTemplate planTemplate;
        try {
            planTemplate = converter.getEventPlanTemplate(project, configuration);
        } catch (EmptyComponentsListException e) {
            return helper.buildStatus(Response.Status.NOT_ACCEPTABLE);
        } catch (NullArgumentException e) {
            return helper.buildStatus(Response.Status.NOT_ACCEPTABLE);
        }

        Entity resultPlan;
        try {
            resultPlan = activeObjectsService.addPlan(planTemplate);
        } catch (ActiveObjectSavingException e) {
            return helper.buildStatus(Response.Status.CONFLICT);
        }
        if (resultPlan == null) {
            return helper.buildStatus(Response.Status.CONFLICT);
        }

        return helper.buildStatus(Response.Status.OK);
    }

    /**
     * @param request Http Servlet request.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     * @see {@link RestManager#get(HttpServletRequest)}
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context final HttpServletRequest request) {
        return super.get(request);
    }

    /**
     * @param id Id of Plan to delete. If not specified nothing should be deleted.
     * @return Response which indicates that action was successful or not (and why) coded by numbers (formed with HTTP response standard).
     * @see {@link RestManager#delete(Class, String, HttpServletRequest)}
     */
    @DELETE
    @Consumes(MediaType.TEXT_PLAIN)
    public Response delete(String id, @Context final HttpServletRequest request) {
        return super.delete(entityType, id, request);
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class NewPlanTemplateConfiguration {
        @XmlElement
        private String projectKey;
        @XmlElement
        private String name;
        @XmlElement
        private String description;
        @XmlElement
        private String reserveTime;
        @XmlElement
        private List<String> categories;

        public NewPlanTemplateConfiguration() {
            this("", "", "", "", new ArrayList<String>());
        }

        public NewPlanTemplateConfiguration(String projectKey, String name, String description, String reserveTime, List<String> categories) {
            this.projectKey = projectKey;
            this.name = name;
            this.description = description;
            this.reserveTime = reserveTime;
            this.categories = categories;
        }

        public boolean isFullfilled() {
            return StringUtils.isNotBlank(getName())
                    && StringUtils.isNotBlank(getProjectKey())
                    && TextUtils.isEachElementNotBlank(getCategories().toArray(new String[getCategories().size()]));
        }

        public String getProjectKey() {
            return projectKey;
        }

        public void setProjectKey(String projectKey) {
            this.projectKey = projectKey;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            if (description == null) {
                description = "";
            }
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getReserveTime() {
            return reserveTime;
        }

        public void setReserveTime(String reserveTime) {
            this.reserveTime = reserveTime;
        }

        public List<String> getCategories() {
            return categories;
        }

        public void setCategories(List<String> categories) {
            this.categories = categories;
        }
    }
}
