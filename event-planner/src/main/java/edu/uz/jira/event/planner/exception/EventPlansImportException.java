package edu.uz.jira.event.planner.exception;

/**
 * Exception which indicates problems with importing predefined Event Plans.
 */
public class EventPlansImportException extends Exception {

    /**
     * Constructor.
     *
     * @param cause Cause of exception
     */
    public EventPlansImportException(Exception cause) {
        super(cause);
    }
}
