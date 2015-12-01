package edu.uz.jira.event.planner.exceptions;

import org.apache.commons.lang.StringUtils;

/**
 * Exception which indicates null reference passed to the method which not accepts non-instance references.
 */
public class NullArgumentException extends Exception {
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
        super((argsNames == null || argsNames.length == 0 ? "Arguments" : StringUtils.join(argsNames, ',')) + " must not be null.");
    }
}
