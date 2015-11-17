package edu.uz.jira.event.planner.project;

import com.atlassian.jira.bc.workflow.WorkflowTransitionService;
import com.atlassian.jira.blueprint.api.*;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.layout.field.*;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.condition.SubTaskBlockingCondition;
import com.opensymphony.workflow.loader.ConditionDescriptor;
import com.opensymphony.workflow.loader.DescriptorFactory;
import com.opensymphony.workflow.loader.FunctionDescriptor;
import edu.uz.jira.event.planner.workflow.UpdateDueDatePostFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Map;

public class EventOrganizationProjectHook implements AddProjectHook {
    private final static Logger LOGGER = LoggerFactory.getLogger(EventOrganizationProjectHook.class);
    private final WorkflowTransitionService workflowTransitionService;
    private final FunctionDescriptor postFunctionDescriptor;
    private final ConditionDescriptor conditionDescriptor;

    public EventOrganizationProjectHook(WorkflowTransitionService workflowTransitionService) {
        this.workflowTransitionService = workflowTransitionService;
        this.postFunctionDescriptor = createUpdateDueDatePostFunctionDescriptor();
        this.conditionDescriptor = createSubTaskBlockingConditionDescriptor();
    }

    private FunctionDescriptor createUpdateDueDatePostFunctionDescriptor() {
        FunctionDescriptor postFunctionDescriptor = DescriptorFactory.getFactory().createFunctionDescriptor();
        postFunctionDescriptor.setType("class");

        final Map functionArguments = postFunctionDescriptor.getArgs();
        functionArguments.put("class.name", UpdateDueDatePostFunction.class.getName());

        return postFunctionDescriptor;
    }

    private ConditionDescriptor createSubTaskBlockingConditionDescriptor() {
        ConditionDescriptor conditionDescriptor = DescriptorFactory.getFactory().createConditionDescriptor();
        conditionDescriptor.setType("class");

        final Map functionArguments = conditionDescriptor.getArgs();
        functionArguments.put("class.name", SubTaskBlockingCondition.class.getName());
        functionArguments.put("statuses", "DONE,LATE DONE");

        return conditionDescriptor;
    }

    @Override
    public ValidateResponse validate(final ValidateData validateData) {
        return ValidateResponse.create();
    }

    @Override
    public ConfigureResponse configure(@Nonnull final ConfigureData configureData) {
        configureWorkflow(configureData);
        configureIssueFields(configureData);

        String redirect = "/browse/" + configureData.project().getKey() + "#selectedTab=com.atlassian.jira.plugin.system.project%3Asummary-panel";
        return ConfigureResponse.create().setRedirect(redirect);
    }

    private void configureIssueFields(ConfigureData configureData) {
        FieldLayoutManager fieldLayoutManager = ComponentAccessor.getFieldLayoutManager();

        EditableFieldLayout fieldLayout = fieldLayoutManager.getEditableDefaultFieldLayout();
        fieldLayout.setName("Event organizing field layout");

        for (FieldLayoutItem eachFieldItem : fieldLayout.getFieldLayoutItems()) {
            setDueDateAsRequired(fieldLayout, eachFieldItem);
        }

        fieldLayoutManager.storeEditableFieldLayout(fieldLayout);

        FieldLayoutScheme fieldLayoutScheme = fieldLayoutManager.createFieldLayoutScheme("Event organizing field layout scheme", "Field layout scheme for Event organization projects");

        FieldLayoutSchemeEntity entity = fieldLayoutScheme.getEntity(fieldLayout);
        fieldLayoutScheme.addEntity(entity);
    }

    private void setDueDateAsRequired(EditableFieldLayout defaultFieldLayout, FieldLayoutItem eachFieldItem) {
        if (eachFieldItem.getOrderableField().getId().equals("duedate")) {
            defaultFieldLayout.makeRequired(eachFieldItem);
        }
    }

    private void configureWorkflow(@Nonnull ConfigureData configureData) {
        JiraWorkflow eventOrganizationWorkflow = configureData.createdWorkflows().get("EVENT-ORGANIZATION-WORKFLOW");

        workflowTransitionService.addPostFunctionToWorkflow("Deadline exceeded", postFunctionDescriptor, eventOrganizationWorkflow);
        workflowTransitionService.addConditionToWorkflow("Start Progress", conditionDescriptor, eventOrganizationWorkflow);
        workflowTransitionService.addConditionToWorkflow("Start Late Progress", conditionDescriptor, eventOrganizationWorkflow);
    }
}
