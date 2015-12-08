package edu.uz.jira.event.planner.workflow.validators;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;

import java.util.Collections;
import java.util.Map;

/**
 * Fabryka parametrów wejściowych validatora workflow, który nie przyjmuje żadnych argumentów.
 */
public class WorkflowNoInputValidatorFactory extends AbstractWorkflowPluginFactory implements com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory {

    protected void getVelocityParamsForInput(Map velocityParams) {
    }

    protected void getVelocityParamsForEdit(Map velocityParams, AbstractDescriptor descriptor) {
    }

    protected void getVelocityParamsForView(Map velocityParams, AbstractDescriptor descriptor) {
    }

    /**
     * @see {@link com.atlassian.jira.plugin.workflow.WorkflowPluginValidatorFactory#getDescriptorParams(Map)}
     */
    public Map getDescriptorParams(Map conditionParams) {
        return Collections.EMPTY_MAP;
    }
}

