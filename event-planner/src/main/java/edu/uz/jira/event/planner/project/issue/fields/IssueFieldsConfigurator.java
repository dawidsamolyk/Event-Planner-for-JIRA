package edu.uz.jira.event.planner.project.issue.fields;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutScheme;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutSchemeEntity;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.project.Project;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.util.text.Internationalization;
import org.ofbiz.core.entity.GenericEntityException;

import javax.annotation.Nonnull;

/**
 * Configurator of Issue Fields Layout.
 */
public class IssueFieldsConfigurator {
    private final FieldLayoutManager fieldLayoutManager;
    private final I18nResolver internationalization;
    private final FieldLayoutBuilder fieldLayoutBuilder;

    /**
     * Constructor.
     *
     * @param i18n Injected {@code I18nResolver} implementation.
     */
    public IssueFieldsConfigurator(@Nonnull final I18nResolver i18n) {
        this.internationalization = i18n;
        this.fieldLayoutBuilder = new FieldLayoutBuilder(i18n);
        this.fieldLayoutManager = ComponentAccessor.getFieldLayoutManager();
    }

    /**
     * @param requiredTaskFieldsIds Required Task fields IDs.
     * @return Pre-configured Event Organization Plan Field Layout.
     */
    public EditableFieldLayout getFieldLayoutCopyWithRequiredFields(@Nonnull final String[] requiredTaskFieldsIds) {
        EditableFieldLayout defaultFieldLayout = fieldLayoutManager.getEditableDefaultFieldLayout();
        return fieldLayoutBuilder.copyWithMakeRequired(defaultFieldLayout, requiredTaskFieldsIds);
    }

    /**
     * @param fieldLayout Pre-configured Event Organization Plan Field Layout which will be stored on JIRA.
     * @return Stored field layout.
     */
    public EditableFieldLayout storeAndReturn(@Nonnull final EditableFieldLayout fieldLayout) throws GenericEntityException {
        return fieldLayoutManager.storeAndReturnEditableFieldLayout(fieldLayout);
    }

    /**
     * @param project     Project.
     * @param fieldLayout Pre-configured Event Organization Plan Field Layout.
     * @return Field Layout Scheme associated with input project's issues types.
     */
    public FieldLayoutScheme createConfigurationScheme(@Nonnull final Project project, @Nonnull final EditableFieldLayout fieldLayout) {
        String name = getInternationalized(Internationalization.PROJECT_FIELDS_CONFIGURATION_SCHEME_NAME) + " " + project.getName();
        String description = getInternationalized(Internationalization.PROJECT_FIELDS_CONFIGURATION_SCHEME_DESCRIPTION);
        Long fieldLayoutId = fieldLayout.getId();

        FieldLayoutScheme result = fieldLayoutManager.createFieldLayoutScheme(name, description);

        for (IssueType eachIssueType : project.getIssueTypes()) {
            String eachIssueTypeId = eachIssueType.getId();
            FieldLayoutSchemeEntity entity = fieldLayoutManager.createFieldLayoutSchemeEntity(result, eachIssueTypeId, fieldLayoutId);

            result.addEntity(entity);
        }

        return result;
    }

    private String getInternationalized(@Nonnull final String key) {
        return internationalization.getText(key);
    }

    /**
     * Adds scheme association with project and removes default scheme association.
     *
     * @param project           Project.
     * @param fieldLayoutScheme Field Layout Scheme which will be associated with input project.
     */
    public void addSchemeAssociation(@Nonnull final Project project, @Nonnull final FieldLayoutScheme fieldLayoutScheme) {
        fieldLayoutManager.addSchemeAssociation(project, fieldLayoutScheme.getId());
    }
}
