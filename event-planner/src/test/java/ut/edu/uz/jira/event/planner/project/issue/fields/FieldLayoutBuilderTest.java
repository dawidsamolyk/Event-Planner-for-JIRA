package ut.edu.uz.jira.event.planner.project.issue.fields;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.MockFieldManager;
import com.atlassian.jira.issue.fields.OrderableField;
import com.atlassian.jira.issue.fields.layout.field.FieldDescriptionHelper;
import com.atlassian.jira.issue.fields.layout.field.FieldLayout;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutItem;
import com.atlassian.jira.issue.fields.layout.field.FieldLayoutManager;
import com.atlassian.jira.issue.fields.renderer.HackyFieldRendererRegistry;
import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.ofbiz.MockOfBizDelegator;
import com.atlassian.jira.ofbiz.OfBizDelegator;
import com.atlassian.sal.api.message.I18nResolver;
import edu.uz.jira.event.planner.project.issue.fields.FieldLayoutBuilder;
import edu.uz.jira.event.planner.util.text.Internationalization;
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
                .addMock(FieldManager.class, new MockFieldManager())
                .addMock(FieldDescriptionHelper.class, mock(FieldDescriptionHelper.class))
                .init();

        Mockito.when(mocki18n.getText(Internationalization.PROJECT_FIELDS_CONFIGURATION_SCHEME_NAME)).thenReturn("Event organization Field Configuration");
        Mockito.when(mocki18n.getText(Internationalization.PROJECT_FIELDS_CONFIGURATION_SCHEME_DESCRIPTION)).thenReturn("Field Configuration for the Event organization Issues");
    }

    @Test
    public void should_Copy_Field_Layout_With_Only_One_Required_Field() {
        String[] ModifiedFieldId = {"non-required-field-which-should-be-required"};
        FieldLayout toCopy = mock(FieldLayout.class);
        List<FieldLayoutItem> toCopyItems = new ArrayList<FieldLayoutItem>();
        toCopyItems.add(getMockItem("required-field", true));
        toCopyItems.add(getMockItem("non-required-field", false));
        toCopyItems.add(getMockItem(ModifiedFieldId[0], false));
        Mockito.when(toCopy.getFieldLayoutItems()).thenReturn(toCopyItems);
        FieldLayoutBuilder fixture = new FieldLayoutBuilder(mocki18n);

        FieldLayout result = fixture.copyWithMakeRequired(toCopy, ModifiedFieldId);

        assertTrue(result.getFieldLayoutItem(ModifiedFieldId[0]).isRequired());
    }

    @Test
    public void should_Copy_Field_Layout_With_Only_Many_Required_Fields() {
        String firstModifiedFieldId = "first-non-required-field-which-should-be-required";
        String secondModifiedFieldId = "second-non-required-field-which-should-be-required";
        String thirdModifiedFieldId = "second-required-field-which-should-be-still-required";
        FieldLayout toCopy = mock(FieldLayout.class);
        List<FieldLayoutItem> toCopyItems = new ArrayList<FieldLayoutItem>();
        toCopyItems.add(getMockItem("required-field", true));
        toCopyItems.add(getMockItem(firstModifiedFieldId, false));
        toCopyItems.add(getMockItem("non-required-field", false));
        toCopyItems.add(getMockItem(secondModifiedFieldId, false));
        toCopyItems.add(getMockItem(thirdModifiedFieldId, true));
        Mockito.when(toCopy.getFieldLayoutItems()).thenReturn(toCopyItems);
        FieldLayoutBuilder fixture = new FieldLayoutBuilder(mocki18n);

        FieldLayout result = fixture.copyWithMakeRequired(toCopy, new String[]{firstModifiedFieldId, secondModifiedFieldId, thirdModifiedFieldId});

        assertTrue(result.getFieldLayoutItem(firstModifiedFieldId).isRequired());
        assertTrue(result.getFieldLayoutItem(secondModifiedFieldId).isRequired());
        assertTrue(result.getFieldLayoutItem(thirdModifiedFieldId).isRequired());
    }

    @Test
    public void should_Copy_Field_Layout_Without_Modifications_When_Any_Field_Should_Not_Be_Modified() {
        FieldLayout toCopy = mock(FieldLayout.class);
        List<FieldLayoutItem> toCopyItems = new ArrayList<FieldLayoutItem>();
        toCopyItems.add(getMockItem("required-field", true));
        toCopyItems.add(getMockItem("non-required-field", false));
        toCopyItems.add(getMockItem("second-non-required-field", false));
        Mockito.when(toCopy.getFieldLayoutItems()).thenReturn(toCopyItems);
        FieldLayoutBuilder fixture = new FieldLayoutBuilder(mocki18n);

        FieldLayout result = fixture.copyWithMakeRequired(toCopy, new String[]{"required-field"});

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
