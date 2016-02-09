package edu.uz.jira.event.planner.exception;

/**
 * Exception which indicates null reference passed to the method which not accepts non-instance references.
 */
public class NullArgumentException extends Exception {

    /**
     * Constructor.
     *
     * @param className Null argument name (name of class).
     */
    public NullArgumentException(final String className) {
        super("Argument " + className + " must not be null.");
    }

    public NullArgumentException() {
        super();
    }
}
