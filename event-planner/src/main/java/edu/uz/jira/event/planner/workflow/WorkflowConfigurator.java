package edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.JiraException;
import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import com.opensymphony.workflow.loader.ValidatorDescriptor;
import edu.uz.jira.event.planner.util.text.TextUtils;

import javax.annotation.Nonnull;

/**
 * Configures workflow.
 */
public class WorkflowConfigurator {
    private final JiraAuthenticationContext jiraAuthenticationContext;
    private final WorkflowTransitionService workflowTransitionService;
    private final WorkflowManager workflowManager;

    /**
     * Constructor.
     *
     * @param workflowTransitionService Injected {@code WorkflowTransitionService} implementation.
     */
    public WorkflowConfigurator(@Nonnull final WorkflowTransitionService workflowTransitionService) {
        this.jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext();
        this.workflowTransitionService = workflowTransitionService;
        this.workflowManager = ComponentAccessor.getWorkflowManager();
    }

    /**
     * @param workflow         Worflow to configure.
     * @param postFunction     Post-function to addFrom.
     * @param transitionsNames Names of the transitions to which post-function should be added.
     */
    public void addToDraft(@Nonnull final JiraWorkflow workflow, @Nonnull final FunctionDescriptor postFunction, @Nonnull final String... transitionsNames) throws JiraException {
        for (String eachTransitionName : transitionsNames) {
            ErrorCollection errors = workflowTransitionService.addPostFunctionToWorkflow(eachTransitionName, postFunction, workflow);

            if (errors.hasAnyErrors()) {
                throw new JiraException(getJoined(errors));
            }
        }
    }

    /**
     * @param workflow         Worflow to configure.
     * @param condition        Condition.
     * @param transitionsNames Names of the transitions to which condition should be added.
     */
    public void addToDraft(@Nonnull final JiraWorkflow workflow, @Nonnull final ConditionDescriptor condition, @Nonnull final String... transitionsNames) throws JiraException {
        for (String eachTransitionName : transitionsNames) {
            ErrorCollection errors = workflowTransitionService.addConditionToWorkflow(eachTransitionName, condition, workflow);

            if (errors.hasAnyErrors()) {
                throw new JiraException(getJoined(errors));
            }
        }
    }

    private String getJoined(@Nonnull final ErrorCollection errors) {
        return TextUtils.getJoined(errors.getErrorMessages(), ' ');
    }

    /**
     * @param workflow         Worflow to configure.
     * @param validator        Validator to addFrom.
     * @param transitionsNames Names of the transitions to which validator should be added.
     */
    public void addToDraft(@Nonnull final JiraWorkflow workflow, @Nonnull final ValidatorDescriptor validator, @Nonnull final String... transitionsNames) throws JiraException {
        JiraWorkflow draft = getDraft(workflow);

        if (draft != null) {
            for (String eachTransitionName : transitionsNames) {
                for (ActionDescriptor eachAction : draft.getActionsByName(eachTransitionName)) {
                    eachAction.getValidators().add(0, validator);
                }
            }
            update(draft);
        } else {
            String message = ComponentAccessor.getJiraAuthenticationContext().getI18nHelper().getText("admin.workflowtransitions.service.error.null.workflow");
            throw new JiraException(message);
        }
    }

    private JiraWorkflow getDraft(@Nonnull final JiraWorkflow workflow) {
        ApplicationUser user = jiraAuthenticationContext.getUser();
        String workflowName = workflow.getName();

        JiraWorkflow draft = workflowManager.getDraftWorkflow(workflowName);

        if (draft == null) {
            draft = workflowManager.createDraftWorkflow(user, workflowName);
        }
        return draft;
    }

    private void update(@Nonnull final JiraWorkflow workflow) {
        workflowManager.updateWorkflow(jiraAuthenticationContext.getUser(), workflow);
    }

    /**
     * @param workflow Workflow to publish.
     */
    public void publishDraft(@Nonnull final JiraWorkflow workflow) {
        workflowManager.overwriteActiveWorkflow(workflow.getUpdateAuthor(), workflow.getName());
    }
}
