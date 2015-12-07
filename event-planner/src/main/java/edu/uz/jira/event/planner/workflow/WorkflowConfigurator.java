package edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.JiraException;
import com.atlassian.jira.bc.JiraServiceContextImpl;
import com.atlassian.jira.bc.workflow.WorkflowService;
import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Nonnull;

/**
 * Configures workflow.
 */
public class WorkflowConfigurator {
    private static final WorkflowService WORKFLOW_SERVICE = ComponentAccessor.getComponentOfType(WorkflowService.class);
    private static final JiraAuthenticationContext AUTHENTICATION_CONTEXT = ComponentAccessor.getJiraAuthenticationContext();
    private final WorkflowTransitionService WORKFLOW_TRANSITION_SERVICE;

    /**
     * @param workflowTransitionService Service which manages JIRA workflows.
     */
    public WorkflowConfigurator(@Nonnull final WorkflowTransitionService workflowTransitionService) {
        this.WORKFLOW_TRANSITION_SERVICE = workflowTransitionService;
    }

    /**
     * @param workflow         Worflow to configure.
     * @param condition        Condition to add.
     * @param transitionsNames Names of the transitions to which condition should be added.
     */
    public void addToDraft(@Nonnull final JiraWorkflow workflow, @Nonnull final ConditionDescriptor condition, @Nonnull final String... transitionsNames) throws JiraException {
        for (String eachTransitionName : transitionsNames) {
            ErrorCollection errors = WORKFLOW_TRANSITION_SERVICE.addConditionToWorkflow(eachTransitionName, condition, workflow);

            if (errors.hasAnyErrors()) {
                throw new JiraException(StringUtils.join(errors.getErrorMessages(), ' '));
            }
        }
    }

    /**
     * @param workflow         Worflow to configure.
     * @param validator        Validator to add.
     * @param transitionsNames Names of the transitions to which validator should be added.
     */
    public void addToDraft(@Nonnull final JiraWorkflow workflow, @Nonnull final ValidatorDescriptor validator, @Nonnull final String... transitionsNames) {
        JiraWorkflow draft = getDraft(workflow);

        for (String eachTransitionName : transitionsNames) {
            for (ActionDescriptor eachAction : draft.getActionsByName(eachTransitionName)) {
                eachAction.getValidators().add(0, validator);
            }
        }

        update(draft);
    }

    private JiraWorkflow getDraft(@Nonnull final JiraWorkflow workflow) {
        JiraServiceContextImpl jiraServiceContext = getJiraServiceContext();
        String workflowName = workflow.getName();

        JiraWorkflow draft = WORKFLOW_SERVICE.getDraftWorkflow(jiraServiceContext, workflowName);

        if (draft == null) {
            draft = WORKFLOW_SERVICE.createDraftWorkflow(jiraServiceContext, workflowName);
        }
        return draft;
    }

    private void update(@Nonnull final JiraWorkflow workflow) {
        WORKFLOW_SERVICE.updateWorkflow(getJiraServiceContext(), workflow);
    }

    private JiraServiceContextImpl getJiraServiceContext() {
        return new JiraServiceContextImpl(AUTHENTICATION_CONTEXT.getUser());
    }

    /**
     * @param workflow         Worflow to configure.
     * @param postFunction     Post-function to add.
     * @param transitionsNames Names of the transitions to which post-function should be added.
     */
    public void addToDraft(@Nonnull final JiraWorkflow workflow, @Nonnull final FunctionDescriptor postFunction, @Nonnull final String... transitionsNames) throws JiraException {
        for (String eachTransitionName : transitionsNames) {
            ErrorCollection errors = WORKFLOW_TRANSITION_SERVICE.addPostFunctionToWorkflow(eachTransitionName, postFunction, workflow);

            if (errors.hasAnyErrors()) {
                throw new JiraException(StringUtils.join(errors.getErrorMessages(), ' '));
            }
        }
    }

    /**
     * @param workflow Workflow to publish.
     */
    public void publishDraft(@Nonnull final JiraWorkflow workflow) {
        ComponentAccessor.getWorkflowManager().overwriteActiveWorkflow(workflow.getUpdateAuthor(), workflow.getName());
    }
}
