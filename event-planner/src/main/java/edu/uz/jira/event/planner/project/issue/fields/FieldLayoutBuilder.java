package edu.uz.jira.event.planner.project.issue.fields;

import com.atlassian.jira.issue.fields.OrderableField;
import com.atlassian.jira.issue.fields.layout.field.EditableFieldLayoutImpl;
import com.atlassian.jira.issue.fields.layout.field.FieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItemImpl;
import com.atlassian.sal.api.message.I18nResolver;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Builder of the Issue Field Layout.
 */
public class FieldLayoutBuilder {
    private final I18nResolver i18n;

    /**
     * @param i18n Internationalization resolver.
     */
    public FieldLayoutBuilder(@Nonnull final I18nResolver i18n) {
        this.i18n = i18n;
    }

    /**
     * @param toCopy            Source Field Layout which delivers field items.
     * @param requiredFieldsIds IDs of fields which should be required in result Field Layout.
     * @return Modified Field Layout with required fields (if typed).
     */
    public FieldLayout copyWithMakeRequired(@Nonnull final FieldLayout toCopy, @Nonnull final String... requiredFieldsIds) {
        List<FieldLayoutItem> toCopyItems = toCopy.getFieldLayoutItems();
        List<FieldLayoutItem> resultFieldLayoutItems = new ArrayList<FieldLayoutItem>(toCopyItems.size());
        List<String> requiredFieldsIdsList = Arrays.asList(requiredFieldsIds);

        for (FieldLayoutItem eachItem : toCopyItems) {
            FieldLayoutItemImpl item = createFieldLayoutItem(eachItem, requiredFieldsIdsList);
            resultFieldLayoutItems.add(item);
        }

        EditableFieldLayoutImpl result = new EditableFieldLayoutImpl(null, resultFieldLayoutItems);
        result.setName(i18n.getText("project.fields.configuration.name"));
        result.setDescription(i18n.getText("project.fields.configuration.description"));

        return result;
    }

    /**
     * @param item                  Field layout item.
     * @param requiredFieldsIdsList IDs of fields which should be required in result Field Layout.
     * @return Result item (with required flag if ID was on input list).
     */
    private FieldLayoutItemImpl createFieldLayoutItem(@Nonnull final FieldLayoutItem item, @Nonnull final List<String> requiredFieldsIdsList) {
        boolean required = isRequired(requiredFieldsIdsList, item);

        return new FieldLayoutItemImpl.Builder().
                setFieldDescription(item.getFieldDescription()).
                setHidden(item.isHidden()).
                setRequired(required).
                setRendererType(item.getRendererType()).
                setOrderableField(item.getOrderableField()).
                build();
    }

    /**
     * @param requiredFieldsIdsList IDs of fields which should be required in result Field Layout.
     * @param item                  Field layout item.
     * @return Indicates if field should be marked as required.
     */
    private boolean isRequired(@Nonnull final List<String> requiredFieldsIdsList, @Nonnull final FieldLayoutItem item) {
        boolean required = item.isRequired();

        OrderableField orderableField = item.getOrderableField();
        if (requiredFieldsIdsList.contains(orderableField.getId())) {
            required = true;
        }

        return required;
    }
}