package edu.uz.jira.event.planner.project.issue.fields;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutSchemeEntity;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.util.Internationalization;

import javax.annotation.Nonnull;

/**
 * Configurator of Issue Fields Layout.
 */
public class IssueFieldsConfigurator {
    private static final String DUE_DATE_FIELD_ID = "duedate";
    private final FieldLayoutManager FIELD_LAYOUT_MANAGER;
    private final I18nResolver INTERNATIONALIZATION;
    private final FieldLayoutBuilder fieldLayoutBuilder;

    /**
     * Constructor.
     *
     * @param i18n Injected {@code I18nResolver} implementation.
     */
    public IssueFieldsConfigurator(@Nonnull final I18nResolver i18n) {
        this.INTERNATIONALIZATION = i18n;
        this.fieldLayoutBuilder = new FieldLayoutBuilder(i18n);
        this.FIELD_LAYOUT_MANAGER = ComponentAccessor.getFieldLayoutManager();
    }

    /**
     * @return Pre-configured Event Organization Plan Field Layout.
     */
    public EditableFieldLayout getEventOrganizationFieldLayout() {
        EditableFieldLayout defaultFieldLayout = FIELD_LAYOUT_MANAGER.getEditableDefaultFieldLayout();
        return fieldLayoutBuilder.copyWithMakeRequired(defaultFieldLayout, DUE_DATE_FIELD_ID);
    }

    /**
     * @param fieldLayout Pre-configured Event Organization Plan Field Layout which will be stored on JIRA.
     * @return Stored field layout.
     */
    public EditableFieldLayout storeAndReturnEventOrganizationFieldLayout(@Nonnull final EditableFieldLayout fieldLayout) {
        return FIELD_LAYOUT_MANAGER.storeAndReturnEditableFieldLayout(fieldLayout);
    }

    /**
     * @param project     Project.
     * @param fieldLayout Pre-configured Event Organization Plan Field Layout.
     * @return Field Layout Scheme associated with input project's issues types.
     */
    public FieldLayoutScheme createFieldConfigurationScheme(@Nonnull final Project project, @Nonnull final EditableFieldLayout fieldLayout) {
        String name = getInternationalized(Internationalization.PROJECT_FIELDS_CONFIGURATION_SCHEME_NAME) + " " + project.getName();
        String description = getInternationalized(Internationalization.PROJECT_FIELDS_CONFIGURATION_SCHEME_DESCRIPTION);
        Long fieldLayoutId = fieldLayout.getId();

        FieldLayoutScheme result = FIELD_LAYOUT_MANAGER.createFieldLayoutScheme(name, description);

        for (IssueType eachIssueType : project.getIssueTypes()) {
            String eachIssueTypeId = eachIssueType.getId();
            FieldLayoutSchemeEntity entity = FIELD_LAYOUT_MANAGER.createFieldLayoutSchemeEntity(result, eachIssueTypeId, fieldLayoutId);

            result.addEntity(entity);
        }

        return result;
    }

    private String getInternationalized(@Nonnull final String key) {
        return INTERNATIONALIZATION.getText(key);
    }

    /**
     * Adds scheme association with project and removes default scheme association.
     *
     * @param project           Project.
     * @param fieldLayoutScheme Field Layout Scheme which will be associated with input project.
     */
    public void storeFieldConfigurationScheme(@Nonnull final Project project, @Nonnull final FieldLayoutScheme fieldLayoutScheme) {
        FIELD_LAYOUT_MANAGER.addSchemeAssociation(project, fieldLayoutScheme.getId());
    }
}
