package edu.uz.jira.event.planner.project.issue;

import com.atlassian.jira.blueprint.api.ConfigureData;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.OrderableField;
import com.atlassian.jira.issue.fields.layout.field.*;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;

import java.util.ArrayList;
import java.util.List;

public class IssueFieldsConfigurator {
    private static final FieldLayoutManager FIELD_LAYOUT_MANAGER = ComponentAccessor.getFieldLayoutManager();
    private static final EditableFieldLayout EVENT_ORGANIZATION_FIELD_LAYOUT;

    static {
        EVENT_ORGANIZATION_FIELD_LAYOUT = FIELD_LAYOUT_MANAGER.storeAndReturnEditableFieldLayout(copyDefaultFieldLayoutButWithRequiredDueDate());
    }

    private static EditableFieldLayout copyDefaultFieldLayoutButWithRequiredDueDate() {
        EditableFieldLayout defaultFieldLayout = FIELD_LAYOUT_MANAGER.getEditableDefaultFieldLayout();
        List<FieldLayoutItem> defaultFieldLayoutItems = defaultFieldLayout.getFieldLayoutItems();

        List<FieldLayoutItem> resultFieldLayoutItems = new ArrayList<FieldLayoutItem>(defaultFieldLayoutItems.size());

        for (FieldLayoutItem eachItem : defaultFieldLayoutItems) {
            boolean required = eachItem.isRequired();
            OrderableField orderableField = eachItem.getOrderableField();
            if (orderableField.getId().equals("duedate")) {
                required = true;
            }

            FieldLayoutItemImpl field = new FieldLayoutItemImpl.Builder().
                    setFieldDescription(eachItem.getFieldDescription()).
                    setHidden(eachItem.isHidden()).
                    setRequired(required).
                    setRendererType(eachItem.getRendererType()).
                    setOrderableField(orderableField).
                    build();
            resultFieldLayoutItems.add(field);
        }

        EditableFieldLayoutImpl result = new EditableFieldLayoutImpl(null, resultFieldLayoutItems);

        result.setName("Event organization Field Configuration");
        result.setDescription("Field Configuration for the Event organization issues");

        return result;
    }

    public void addFieldConfigurationScheme(ConfigureData configureData) {
        Project project = configureData.project();

        FieldLayoutScheme fieldLayoutScheme = createFieldConfigurationScheme(project);
        FIELD_LAYOUT_MANAGER.addSchemeAssociation(project, fieldLayoutScheme.getId());
        FIELD_LAYOUT_MANAGER.removeSchemeAssociation(project, FIELD_LAYOUT_MANAGER.getEditableDefaultFieldLayout().getId());
    }
    
    private FieldLayoutScheme createFieldConfigurationScheme(final Project project) {
        String resultName = "Event organizing Field Configuration Scheme for project " + project.getName();
        String resultDescription = "Field Configuration Scheme for Event organization projects";

        FieldLayoutScheme result = FIELD_LAYOUT_MANAGER.createFieldLayoutScheme(resultName, resultDescription);

        for (IssueType eachIssueType : project.getIssueTypes()) {
            String eachIssueTypeId = eachIssueType.getId();
            Long fieldConfigId = EVENT_ORGANIZATION_FIELD_LAYOUT.getId();
            FieldLayoutSchemeEntity entity = FIELD_LAYOUT_MANAGER.createFieldLayoutSchemeEntity(result, eachIssueTypeId, fieldConfigId);

            result.addEntity(entity);
        }

        return result;
    }
}
