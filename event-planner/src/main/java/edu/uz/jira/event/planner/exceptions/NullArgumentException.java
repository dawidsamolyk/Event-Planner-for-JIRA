package edu.uz.jira.event.planner.exceptions;

import edu.uz.jira.event.planner.util.TextUtils;

import javax.annotation.Nonnull;

/**
 * Exception which indicates null reference passed to the method which not accepts non-instance references.
 */
public class NullArgumentException extends Exception {
    private final static TextUtils textUtils = new TextUtils();

    /**
     * @param className Null argument name (name of class).
     */
    public NullArgumentException(@Nonnull final String className) {
        super("Argument " + className + " must not be null.");
    }
}
