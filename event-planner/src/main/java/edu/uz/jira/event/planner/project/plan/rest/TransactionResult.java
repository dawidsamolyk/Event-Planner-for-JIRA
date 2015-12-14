package edu.uz.jira.event.planner.project.plan.rest;

/**
 * Database transaction/operation result.
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
     * Setting result to VALID.
     */
    public void setValid() {
        state = State.VALID;
    }

    /**
     * Setting result to ERROR.
     */
    public void setError() {
        state = State.ERROR;
    }

    /**
     * @return Is result valid.
     */
    public boolean isValid() {
        return state.equals(State.VALID);
    }

    /**
     * State of transaction.
     */
    public enum State {
        UNKNOWN, VALID, ERROR;
    }
}
