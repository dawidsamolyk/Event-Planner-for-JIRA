package edu.uz.jira.event.planner.project.plan.webwork;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.database.active.objects.ActiveObjectsService;
import edu.uz.jira.event.planner.database.xml.importer.EventPlansImportExecutor;
import webwork.action.Action;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Webwork which handles Event Plan Configuration page.
 */
public class EventPlansManager extends JiraWebActionSupport {
    private final ActiveObjectsService activeObjectsService;
    private final I18nResolver i18nResolver;

    /**
     * Constructor.
     *
     * @param i18nResolver         Injected {@code I18nResolver} implementation.
     * @param activeObjectsService Injected {@code ActiveObjectsService} implementation.
     */
    public EventPlansManager(@Nonnull final I18nResolver i18nResolver,
                             @Nonnull final ActiveObjectsService activeObjectsService) {
        this.i18nResolver = i18nResolver;
        this.activeObjectsService = activeObjectsService;
    }

    @Override
    public String execute() {
        importPredefinedEventPlansIfRequired();
        return Action.INPUT;
    }

    private void importPredefinedEventPlansIfRequired() {
        EventPlansImportExecutor importExecutor = new EventPlansImportExecutor(i18nResolver, activeObjectsService);
        importExecutor.startImport();
    }

    public Map<String, String> getProjects() {
        Map<String, String> result = new HashMap<String, String>();

        for (Project eachProject : ComponentAccessor.getProjectManager().getProjectObjects()) {
            result.put(eachProject.getKey(), eachProject.getName());
        }

        return result;
    }
}