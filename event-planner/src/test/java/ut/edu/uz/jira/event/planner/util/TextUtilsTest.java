package ut.edu.uz.jira.event.planner.util;

import edu.uz.jira.event.planner.util.text.TextUtils;
import org.apache.commons.collections.ListUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class TextUtilsTest {

    @Test
    public void should_Return_Empty_Text_If_Null_Collection_Should_Be_Joined() {
        TextUtils fixture = new TextUtils();

        String result = fixture.getJoined((String[]) null, ' ');

        assertEquals("", result);
    }

    @Test
    public void should_Return_Empty_Text_If_Input_Collection_Is_Empty() {
        TextUtils fixture = new TextUtils();

        String result = fixture.getJoined(ListUtils.EMPTY_LIST, ' ');

        assertEquals("", result);
    }

    @Test
    public void should_Return_Empty_Text_If_Input_Array_Is_Empty() {
        TextUtils fixture = new TextUtils();

        String result = fixture.getJoined(new String[]{}, ' ');

        assertEquals("", result);
    }

    @Test
    public void should_Join_Texts_From_Collection_With_Specified_Separator() {
        TextUtils fixture = new TextUtils();
        Collection<String> texts = new ArrayList<String>(3);
        texts.add("A");
        texts.add("B");
        texts.add("C");

        String result = fixture.getJoined(texts, ',');

        assertEquals("A,B,C", result);
    }

    @Test
    public void should_Join_Texts_From_Array_With_Specified_Separator() {
        TextUtils fixture = new TextUtils();

        String result = fixture.getJoined(new String[]{"A", "B", "C"}, ',');

        assertEquals("A,B,C", result);
    }

    @Test
    public void string_Array_Should_Be_Blank_If_It_Is_Null() {
        TextUtils fixture = new TextUtils();

        boolean result = fixture.isEachElementNotBlank(null);

        assertEquals(false, result);
    }

    @Test
    public void string_Array_Should_Be_Blank_If_Is_Empty() {
        TextUtils fixture = new TextUtils();

        boolean result = fixture.isEachElementNotBlank(new String[]{});

        assertEquals(false, result);
    }

    @Test
    public void string_Array_Should_Be_Blank_If_Any_Text_Is_Blank() {
        TextUtils fixture = new TextUtils();

        boolean result = fixture.isEachElementNotBlank(new String[]{"Not blank", "", "any text", "test"});

        assertEquals(false, result);
    }

    @Test
    public void string_Array_Sgould_Not_Be_Blank_If_Contains_Only_Texts() {
        TextUtils fixture = new TextUtils();

        boolean result = fixture.isEachElementNotBlank(new String[]{"Not blank", "testtesttest", "any text", "test"});

        assertEquals(true, result);
    }
}
