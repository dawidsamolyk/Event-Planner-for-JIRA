package edu.uz.jira.event.planner.project.plan.rest;

/**
 * Database (Active Objects) transaction/operation result.
 */
public class TransactionResult {
    private State state;

    /**
     * Constructor.
     * Default state is UNKNOWN.
     */
    public TransactionResult() {
        state = State.UNKNOWN;
    }

    /**
     * Setting state to VALID.
     */
    public void setValid() {
        state = State.VALID;
    }

    /**
     * Setting state to ERROR.
     */
    public void setError() {
        state = State.ERROR;
    }

    /**
     * @return Is state valid.
     */
    public boolean isValid() {
        return state.equals(State.VALID);
    }

    /**
     * State of transaction.
     */
    private enum State {
        UNKNOWN, VALID, ERROR
    }
}
