package edu.uz.jira.event.planner.project.issue.fields;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.layout.field.*;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.sal.api.message.I18nResolver;

import javax.annotation.Nonnull;

/**
 * Configurator of Issue Fields Layout.
 */
public class IssueFieldsConfigurator {
    public static final String PROJECT_FIELDS_CONFIGURATION_SCHEME_NAME_KEY = "project.fields.configuration.scheme.name";
    public static final String PROJECT_FIELDS_CONFIGURATION_SCHEME_DESCRIPTION_KEY = "project.fields.configuration.scheme.description";
    private static final String DUE_DATE_FIELD_ID = "duedate";
    private static final FieldLayoutManager FIELD_LAYOUT_MANAGER = ComponentAccessor.getFieldLayoutManager();
    private final I18nResolver i18n;
    private final FieldLayoutBuilder fieldLayoutBuilder;
    private FieldLayout eventOrganizationFieldLayout;

    /**
     * @param i18n Internationalization resolver.
     */
    public IssueFieldsConfigurator(@Nonnull final I18nResolver i18n) {
        this.i18n = i18n;
        this.fieldLayoutBuilder = new FieldLayoutBuilder(i18n);
    }

    /**
     * @return Pre-configured Event Organization Plan Field Layout.
     */
    public FieldLayout getEventOrganizationFieldLayout() {
        if (eventOrganizationFieldLayout == null) {
            EditableFieldLayout defaultFieldLayout = FIELD_LAYOUT_MANAGER.getEditableDefaultFieldLayout();
            eventOrganizationFieldLayout = fieldLayoutBuilder.copyWithMakeRequired(defaultFieldLayout, DUE_DATE_FIELD_ID);
        }
        return eventOrganizationFieldLayout;
    }

    /**
     * @param fieldLayout Pre-configured Event Organization Plan Field Layout which will be stored on JIRA.
     */
    public void storeEventOrganizationFieldLayout(@Nonnull final FieldLayout fieldLayout) {
        if (fieldLayout instanceof EditableFieldLayout) {
            if (exists(fieldLayout)) {
                return;
            }
            FIELD_LAYOUT_MANAGER.storeEditableFieldLayout((EditableFieldLayout) fieldLayout);
        }
    }

    private boolean exists(FieldLayout fieldLayout) {
        for (EditableFieldLayout each : FIELD_LAYOUT_MANAGER.getEditableFieldLayouts()) {
            if (each.getName().equals(fieldLayout.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param project     Project.
     * @param fieldLayout Pre-configured Event Organization Plan Field Layout.
     * @return Field Layout Scheme associated with input project's issues types.
     */
    public FieldLayoutScheme createFieldConfigurationScheme(@Nonnull final Project project, @Nonnull final FieldLayout fieldLayout) {
        String name = getInternationalized(PROJECT_FIELDS_CONFIGURATION_SCHEME_NAME_KEY) + " " + project.getName();
        String description = getInternationalized(PROJECT_FIELDS_CONFIGURATION_SCHEME_DESCRIPTION_KEY);

        FieldLayoutScheme result = FIELD_LAYOUT_MANAGER.createFieldLayoutScheme(name, description);

        for (IssueType eachIssueType : project.getIssueTypes()) {
            String eachIssueTypeId = eachIssueType.getId();
            FieldLayoutSchemeEntity entity = FIELD_LAYOUT_MANAGER.createFieldLayoutSchemeEntity(result, eachIssueTypeId, fieldLayout.getId());

            result.addEntity(entity);
        }

        return result;
    }

    private String getInternationalized(@Nonnull final String key) {
        return i18n.getText(key);
    }

    /**
     * Adds scheme association with project and removes default scheme association.
     *
     * @param project           Project.
     * @param fieldLayoutScheme Field Layout Scheme which will be associated with input project.
     */
    public void storeFieldConfigurationScheme(@Nonnull final Project project, @Nonnull final FieldLayoutScheme fieldLayoutScheme) {
        FIELD_LAYOUT_MANAGER.removeSchemeAssociation(project, FIELD_LAYOUT_MANAGER.getEditableDefaultFieldLayout().getId());
        FIELD_LAYOUT_MANAGER.addSchemeAssociation(project, fieldLayoutScheme.getId());
    }
}
