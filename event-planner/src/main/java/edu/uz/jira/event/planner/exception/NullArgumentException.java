package edu.uz.jira.event.planner.exception;

import edu.uz.jira.event.planner.util.text.TextUtils;

/**
 * Exception which indicates null reference passed to the method which not accepts non-instance references.
 */
public class NullArgumentException extends Exception {
    private final static TextUtils textUtils = new TextUtils();

    /**
     * @param className Null argument name (name of class).
     */
    public NullArgumentException(final String className) {
        super("Argument " + className + " must not be null.");
    }
}
