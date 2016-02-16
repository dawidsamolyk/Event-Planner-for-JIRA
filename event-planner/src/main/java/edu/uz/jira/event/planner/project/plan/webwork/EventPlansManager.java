package edu.uz.jira.event.planner.project.plan.webwork;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectCategory;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserManager;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.xml.importer.EventPlansImportExecutor;
import edu.uz.jira.event.planner.project.ProjectUtils;
import edu.uz.jira.event.planner.util.ServletHelper;
import webwork.action.Action;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Webwork which handles Event Plan Configuration page.
 */
public class EventPlansManager extends JiraWebActionSupport {
    private final ActiveObjectsService activeObjectsService;
    private final I18nResolver i18nResolver;
    private final ProjectUtils projectUtils;
    private final ServletHelper servletHelper;
    private ProjectManager projectManager;

    /**
     * Constructor.
     *
     * @param i18nResolver         Injected {@code I18nResolver} implementation.
     * @param activeObjectsService Injected {@code ActiveObjectsService} implementation.
     * @param userManager          Injected {@code UserManager} implementation.
     * @param loginUriProvider     Injected {@code LoginUriProvider} implementation.
     */
    public EventPlansManager(@Nonnull final I18nResolver i18nResolver,
                             @Nonnull final ActiveObjectsService activeObjectsService,
                             @Nonnull final UserManager userManager,
                             @Nonnull final LoginUriProvider loginUriProvider) {
        this.i18nResolver = i18nResolver;
        this.activeObjectsService = activeObjectsService;
        projectManager = ComponentAccessor.getProjectManager();
        projectUtils = new ProjectUtils(i18nResolver);
        servletHelper = new ServletHelper(userManager, loginUriProvider);
    }

    @Override
    public String execute() throws IOException {
        if (servletHelper.hasNotAdminUser(getHttpRequest())) {
            servletHelper.redirectToLogin(getHttpRequest(), getHttpResponse());
        }
        importPredefinedEventPlansIfRequired();
        return Action.INPUT;
    }

    private void importPredefinedEventPlansIfRequired() {
        EventPlansImportExecutor importExecutor = new EventPlansImportExecutor(i18nResolver, activeObjectsService);
        importExecutor.startImport();
    }

    public Map<String, String> getProjects() {
        Map<String, String> result = new HashMap<String, String>();

        ProjectCategory eventPlanProjectCategory = projectUtils.getEventPlanProjectCategory();
        if (eventPlanProjectCategory == null) {
            return result;
        }

        for (Project eachProject : projectManager.getProjectsFromProjectCategory(eventPlanProjectCategory)) {
            result.put(eachProject.getKey(), eachProject.getName());
        }

        return result;
    }
}