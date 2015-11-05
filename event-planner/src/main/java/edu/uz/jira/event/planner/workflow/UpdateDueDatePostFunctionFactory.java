package edu.uz.jira.event.planner.workflow;

import com.atlassian.jira.plugin.workflow.AbstractWorkflowPluginFactory;
import com.atlassian.jira.plugin.workflow.WorkflowPluginFunctionFactory;
import com.opensymphony.workflow.loader.AbstractDescriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the factory class responsible for dealing with the UI for the post-function.
 * This is typically where you put default values into the velocity context and where you store user input.
 */
public class UpdateDueDatePostFunctionFactory extends AbstractWorkflowPluginFactory implements WorkflowPluginFunctionFactory {

    @Override
    protected void getVelocityParamsForInput(Map<String, Object> velocityParams) {
    }

    @Override
    protected void getVelocityParamsForEdit(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
    }

    @Override
    protected void getVelocityParamsForView(Map<String, Object> velocityParams, AbstractDescriptor descriptor) {
    }

    public Map<String, ?> getDescriptorParams(Map<String, Object> formParams) {
        return new HashMap();
    }

}