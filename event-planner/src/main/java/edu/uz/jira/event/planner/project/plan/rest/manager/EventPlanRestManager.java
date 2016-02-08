package edu.uz.jira.event.planner.project.plan.rest.manager;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.bc.project.component.ProjectComponentManager;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.project.version.Version;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.transaction.TransactionCallback;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.xml.exporter.EventPlanExporter;
import edu.uz.jira.event.planner.database.xml.model.*;
import edu.uz.jira.event.planner.exception.ActiveObjectSavingException;
import edu.uz.jira.event.planner.exception.EmptyComponentsListException;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.project.ProjectUtils;
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
import java.util.Calendar;
import java.util.List;

/**
 * REST manager for Event Organization Plans.
 */
@Path("/plan")
public class EventPlanRestManager extends RestManager {
    private final JiraAuthenticationContext authenticationContext;
    private final ProjectManager projectManager;
    private final ProjectComponentManager projectComponentManager;
    private final IssueService issueService;
    private ProjectUtils projectUtils;

    /**
     * Constructor.
     *
     * @param userManager          Injected {@code UserManager} implementation.
     * @param transactionTemplate  Injected {@code TransactionTemplate} implementation.
     * @param activeObjectsService Event Organization Plan Service which manages Active Objects (Plans, Domains, Tasks etc.).
     */
    public EventPlanRestManager(@Nonnull final UserManager userManager,
                                @Nonnull final TransactionTemplate transactionTemplate,
                                @Nonnull final ActiveObjectsService activeObjectsService,
                                @Nonnull final I18nResolver i18nResolver) {
        super(userManager, transactionTemplate, activeObjectsService, PlanTemplate.createEmpty());
        projectUtils = new ProjectUtils(i18nResolver);
        projectManager = ComponentAccessor.getProjectManager();
        projectComponentManager = ComponentAccessor.getProjectComponentManager();
        issueService = ComponentAccessor.getIssueService();
        authenticationContext = ComponentAccessor.getJiraAuthenticationContext();

    }

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
            planTemplate = getEventPlanTemplate(project, configuration);
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

    private PlanTemplate getEventPlanTemplate(@Nonnull final Project project, @Nonnull final NewPlanTemplateConfiguration configuration) throws EmptyComponentsListException, NullArgumentException {
        PlanTemplate result = new PlanTemplate();

        result.setName(configuration.getName());
        result.setDescription(configuration.getDescription());
        result.setReserveTimeInDays(Integer.parseInt(configuration.getReserveTime()));

        List<EventCategory> categories = getEventCategories(configuration);
        result.setEventCategory(categories);

        List<ComponentTemplate> components = getComponentTemplates(project);
        result.setComponent(components);

        if (components.isEmpty()) {
            throw new EmptyComponentsListException();
        }

        return result;
    }

    private List<ComponentTemplate> getComponentTemplates(@Nonnull final Project project) throws NullArgumentException {
        List<ComponentTemplate> components = new ArrayList<ComponentTemplate>();
        for (ProjectComponent eachComponent : project.getProjectComponents()) {
            ComponentTemplate eachComponentTemplate = new ComponentTemplate();
            eachComponentTemplate.setName(eachComponent.getName());
            eachComponentTemplate.setDescription(eachComponent.getDescription());

            List<TaskTemplate> tasks = getTaskTemplates(eachComponent, projectUtils.getDueDateVersion(project));
            eachComponentTemplate.setTask(tasks);

            components.add(eachComponentTemplate);
        }
        return components;
    }

    private List<TaskTemplate> getTaskTemplates(@Nonnull final ProjectComponent eachComponent, @Nonnull final Version dueDateVersion) {
        List<TaskTemplate> result = new ArrayList<TaskTemplate>();

        for (Long issueId : projectComponentManager.getIssueIdsWithComponent(eachComponent)) {
            Issue issue = issueService.getIssue(authenticationContext.getUser(), issueId).getIssue();

            TaskTemplate taskTemplate = new TaskTemplate();
            taskTemplate.setName(issue.getSummary());
            taskTemplate.setDescription(issue.getDescription());

            Calendar issueDueDate = Calendar.getInstance(authenticationContext.getLocale());
            issueDueDate.setTime(issue.getDueDate());
            Calendar projectDueDate = Calendar.getInstance(authenticationContext.getLocale());
            projectDueDate.setTime(dueDateVersion.getReleaseDate());

            int diffYear = projectDueDate.get(Calendar.YEAR) - issueDueDate.get(Calendar.YEAR);
            int diffMonths = (diffYear * 12) + projectDueDate.get(Calendar.MONTH) - issueDueDate.get(Calendar.MONTH);
            int diffDays = projectDueDate.get(Calendar.DAY_OF_YEAR) - issueDueDate.get(Calendar.DAY_OF_YEAR);

            taskTemplate.setNeededMonthsBeforeEvent(diffMonths);
            taskTemplate.setNeededDaysBeforeEvent(diffDays);
            taskTemplate.setSubTask(getSubTaskTemplates(issue));

            result.add(taskTemplate);
        }

        return result;
    }

    private List<SubTaskTemplate> getSubTaskTemplates(@Nonnull final Issue issue) {
        List<SubTaskTemplate> result = new ArrayList<SubTaskTemplate>();

        for (Issue eachSubTask : issue.getSubTaskObjects()) {
            SubTaskTemplate subTask = new SubTaskTemplate();

            subTask.setName(eachSubTask.getSummary());
            subTask.setDescription(eachSubTask.getDescription());

            result.add(subTask);
        }

        return result;
    }

    private List<EventCategory> getEventCategories(@Nonnull final NewPlanTemplateConfiguration configuration) {
        List<EventCategory> categories = new ArrayList<EventCategory>();
        for (String eachCategoryName : configuration.getCategories()) {
            EventCategory eachCategory = new EventCategory();
            eachCategory.setName(eachCategoryName);
            categories.add(eachCategory);
        }
        return categories;
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
