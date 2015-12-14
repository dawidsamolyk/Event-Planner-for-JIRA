package edu.uz.jira.event.planner.exception;

/**
 * Exception which indicates problem with resource in database (Acrive Objects) for example when try to add.
 */
public class ResourceException extends Exception {

    /**
     * Constructor.
     *
     * @param message Message.
     * @param e       Cause of error;
     */
    public ResourceException(String message, Throwable e) {
        super(message, e);
    }
}
