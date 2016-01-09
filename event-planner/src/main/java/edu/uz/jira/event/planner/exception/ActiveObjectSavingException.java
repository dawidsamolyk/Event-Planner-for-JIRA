package edu.uz.jira.event.planner.exception;


public class ActiveObjectSavingException extends Exception {

    public ActiveObjectSavingException() {
        super();
    }

    public ActiveObjectSavingException(String message) {
        super(message);
    }

    public ActiveObjectSavingException(Throwable cause) {
        super(cause);
    }
}
