package edu.uz.jira.event.planner.database.xml.importer;

import com.atlassian.jira.component.ComponentAccessor;

public enum EventPlansImportState {
    IMPORTED("TRUE"), NOT_IMPORTED("FALSE");

    public static final String APPLICATION_PROPERTY_KEY = "EVENT_PLANS_IMPORTED";
    private final String state;

    EventPlansImportState(final String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public static EventPlansImportState getApplicationImportState() {
        String applicationImportState = ComponentAccessor.getApplicationProperties().getText(APPLICATION_PROPERTY_KEY);

        for (EventPlansImportState each : EventPlansImportState.values()) {
            if (each.getState().equals(applicationImportState)) {
                return each;
            }
        }

        return NOT_IMPORTED;
    }


}
