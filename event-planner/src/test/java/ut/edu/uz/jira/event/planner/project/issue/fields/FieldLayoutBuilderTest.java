package ut.edu.uz.jira.event.planner.project.issue.fields;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.OrderableField;
import com.atlassian.jira.issue.fields.layout.field.FieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.renderer.HackyFieldRendererRegistry;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.ofbiz.MockOfBizDelegator;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.project.issue.fields.FieldLayoutBuilder;
import edu.uz.jira.event.planner.utils.Internationalization;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class FieldLayoutBuilderTest {
    private I18nResolver mocki18n;

    @Before
    public void setUp() {
        mocki18n = mock(I18nResolver.class);

        new MockComponentWorker()
                .addMock(ComponentAccessor.class, mock(ComponentAccessor.class))
                .addMock(FieldLayoutManager.class, mock(FieldLayoutManager.class))
                .addMock(HackyFieldRendererRegistry.class, mock(HackyFieldRendererRegistry.class))
                .addMock(OfBizDelegator.class, new MockOfBizDelegator())
                .init();

        Mockito.when(mocki18n.getText(Internationalization.PROJECT_FIELDS_CONFIGURATION_SCHEME_NAME)).thenReturn("Event organization Field Configuration");
        Mockito.when(mocki18n.getText(Internationalization.PROJECT_FIELDS_CONFIGURATION_SCHEME_DESCRIPTION)).thenReturn("Field Configuration for the Event organization Issues");
    }

    @Test
    public void shouldCopyFieldLayoutWithOnlyOneRequiredField() {
        String modifieldFieldId = "non-required-field-which-should-be-required";
        FieldLayout toCopy = mock(FieldLayout.class);
        List<FieldLayoutItem> toCopyItems = new ArrayList<FieldLayoutItem>();
        toCopyItems.add(getMockItem("required-field", true));
        toCopyItems.add(getMockItem("non-required-field", false));
        toCopyItems.add(getMockItem(modifieldFieldId, false));
        Mockito.when(toCopy.getFieldLayoutItems()).thenReturn(toCopyItems);
        FieldLayoutBuilder fixture = new FieldLayoutBuilder(mocki18n);

        FieldLayout result = fixture.copyWithMakeRequired(toCopy, modifieldFieldId);

        assertTrue(result.getFieldLayoutItem(modifieldFieldId).isRequired());
    }

    @Test
    public void shouldCopyFieldLayoutWithOnlyManyRequiredFields() {
        String firstModifieldFieldId = "first-non-required-field-which-should-be-required";
        String secondModifieldFieldId = "second-non-required-field-which-should-be-required";
        String thirdModifieldFieldId = "second-required-field-which-should-be-still-required";
        FieldLayout toCopy = mock(FieldLayout.class);
        List<FieldLayoutItem> toCopyItems = new ArrayList<FieldLayoutItem>();
        toCopyItems.add(getMockItem("required-field", true));
        toCopyItems.add(getMockItem(firstModifieldFieldId, false));
        toCopyItems.add(getMockItem("non-required-field", false));
        toCopyItems.add(getMockItem(secondModifieldFieldId, false));
        toCopyItems.add(getMockItem(thirdModifieldFieldId, true));
        Mockito.when(toCopy.getFieldLayoutItems()).thenReturn(toCopyItems);
        FieldLayoutBuilder fixture = new FieldLayoutBuilder(mocki18n);

        FieldLayout result = fixture.copyWithMakeRequired(toCopy, firstModifieldFieldId, secondModifieldFieldId, thirdModifieldFieldId);

        assertTrue(result.getFieldLayoutItem(firstModifieldFieldId).isRequired());
        assertTrue(result.getFieldLayoutItem(secondModifieldFieldId).isRequired());
        assertTrue(result.getFieldLayoutItem(thirdModifieldFieldId).isRequired());
    }

    @Test
    public void shouldCopyFieldLayoutWithoutModificationsWhenAnyFieldShouldNotBeModified() {
        FieldLayout toCopy = mock(FieldLayout.class);
        List<FieldLayoutItem> toCopyItems = new ArrayList<FieldLayoutItem>();
        toCopyItems.add(getMockItem("required-field", true));
        toCopyItems.add(getMockItem("non-required-field", false));
        toCopyItems.add(getMockItem("second-non-required-field", false));
        Mockito.when(toCopy.getFieldLayoutItems()).thenReturn(toCopyItems);
        FieldLayoutBuilder fixture = new FieldLayoutBuilder(mocki18n);

        FieldLayout result = fixture.copyWithMakeRequired(toCopy);

        assertEquals(true, result.getFieldLayoutItem("required-field").isRequired());
        assertEquals(false, result.getFieldLayoutItem("non-required-field").isRequired());
        assertEquals(false, result.getFieldLayoutItem("second-non-required-field").isRequired());
    }

    private FieldLayoutItem getMockItem(String orderableFieldId, boolean required) {
        FieldLayoutItem result = mock(FieldLayoutItem.class);

        Mockito.when(result.isRequired()).thenReturn(required);

        OrderableField mockOrderableField = mock(OrderableField.class);
        Mockito.when(mockOrderableField.getId()).thenReturn(orderableFieldId);
        Mockito.when(result.getOrderableField()).thenReturn(mockOrderableField);

        return result;
    }
}
