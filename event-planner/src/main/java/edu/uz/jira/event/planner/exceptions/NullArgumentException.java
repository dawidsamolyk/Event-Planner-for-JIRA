package edu.uz.jira.event.planner.exceptions;

import org.apache.commons.lang.StringUtils;

public class NullArgumentException extends Exception {
    public NullArgumentException(String argName) {
        super((argName == null ? "Argument" : argName) + " must not be null.");
    }

    public NullArgumentException(String... argsNames) {
        super((argsNames == null || argsNames.length == 0 ? "Arguments" : StringUtils.join(argsNames, ',')) + " must not be null.");
    }
}
