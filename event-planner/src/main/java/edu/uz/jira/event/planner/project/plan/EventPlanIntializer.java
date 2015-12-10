package edu.uz.jira.event.planner.project.plan;

import javax.annotation.Nonnull;

/**
 * Intializes Event Plans which are predefined by plugin author.
 * <ul>Best practices for developing with Active Objects (from Atlassian):</ul>
 * <li>The Active Objects framework cannot be used during plugin startup.
 * The easiest way to get around this is to perform any initialisation tasks only during the first HTTP request.
 * As ActiveObjects service is not thread-safe during first call, it is also important to ensure other HTTP request threads are blocked while initialisation is being performed.</li>
 */
public class EventPlanIntializer {
    private final EventOrganizationPlanService eventPlanService;

    /**
     * Constructor.
     *
     * @param eventPlanService Service which manages Event Plans.
     */
    public EventPlanIntializer(@Nonnull final EventOrganizationPlanService eventPlanService) {
        this.eventPlanService = eventPlanService;
    }

    /**
     * Initializes predefined Event Plans.
     */
    public void init() {
        // TODO
    }
}
