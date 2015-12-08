package edu.uz.jira.event.planner.exceptions;

import edu.uz.jira.event.planner.utils.TextUtils;

/**
 * Exception which indicates null reference passed to the method which not accepts non-instance references.
 */
public class NullArgumentException extends Exception {
    private final static TextUtils textUtils = new TextUtils();

    /**
     * @param argName Null argument name (name of class).
     */
    public NullArgumentException(String argName) {
        super((argName == null ? "Argument" : argName) + " must not be null.");
    }

    /**
     * @param argsNames Null arguments names (names of classes).
     */
    public NullArgumentException(String... argsNames) {
        super((argsNames == null || argsNames.length == 0 ? "Arguments" : textUtils.getJoined(argsNames, ',')) + " must not be null.");
    }
}
