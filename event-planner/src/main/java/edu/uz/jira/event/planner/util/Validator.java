package edu.uz.jira.event.planner.util;

import edu.uz.jira.event.planner.exception.NullArgumentException;

import javax.servlet.http.HttpServletRequest;

/**
 * Validator for cross-classes method's arguments.
 */
public class Validator {

    /**
     * Checking that http request is not null.
     *
     * @param request Http request.
     * @throws NullArgumentException
     */
    public void check(final HttpServletRequest request) throws NullArgumentException {
        if (request == null) {
            throw new NullArgumentException(HttpServletRequest.class.getName());
        }
    }
}
