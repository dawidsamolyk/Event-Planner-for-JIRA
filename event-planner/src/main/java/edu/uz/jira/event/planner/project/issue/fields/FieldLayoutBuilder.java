package edu.uz.jira.event.planner.project.issue.fields;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.OrderableField;
import com.atlassian.jira.issue.fields.layout.field.*;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.utils.InternationalizationKeys;
import org.apache.commons.collections.MapUtils;
import org.ofbiz.core.entity.GenericValue;

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
    public EditableFieldLayout copyWithMakeRequired(@Nonnull final FieldLayout toCopy, @Nonnull final String... requiredFieldsIds) {
        String name = getInternationalized(InternationalizationKeys.PROJECT_FIELDS_CONFIGURATION_NAME);
        String description = getInternationalized(InternationalizationKeys.PROJECT_FIELDS_CONFIGURATION_DESCRIPTION);
        GenericValue genericValue = ComponentAccessor.getOfBizDelegator().createValue("FieldLayout", MapUtils.EMPTY_MAP);
        genericValue.setString("name", name);
        genericValue.setString("description", description);

        List<FieldLayoutItem> toCopyItems = toCopy.getFieldLayoutItems();
        List<FieldLayoutItem> resultFieldLayoutItems = new ArrayList<FieldLayoutItem>(toCopyItems.size());
        List<String> requiredFieldsIdsList = Arrays.asList(requiredFieldsIds);

        for (FieldLayoutItem eachItem : toCopyItems) {
            FieldLayoutItem item = createFieldLayoutItem(eachItem, requiredFieldsIdsList);
            resultFieldLayoutItems.add(item);
        }

        EditableFieldLayoutImpl result = new EditableFieldLayoutImpl(genericValue, resultFieldLayoutItems);
        result.setName(name);
        result.setDescription(description);

        return result;
    }

    private String getInternationalized(@Nonnull final String key) {
        return i18n.getText(key);
    }

    /**
     * @param item                  Field layout item.
     * @param requiredFieldsIdsList IDs of fields which should be required in result Field Layout.
     * @return Result item (with required flag if ID was on input list).
     */
    private FieldLayoutItem createFieldLayoutItem(@Nonnull final FieldLayoutItem item, @Nonnull final List<String> requiredFieldsIdsList) {
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