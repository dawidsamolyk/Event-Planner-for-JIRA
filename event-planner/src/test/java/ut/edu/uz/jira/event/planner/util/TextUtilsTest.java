package ut.edu.uz.jira.event.planner.util;

import edu.uz.jira.event.planner.util.TextUtils;
import org.apache.commons.collections.ListUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class TextUtilsTest {

    @Test
    public void shouldReturnEmptyTextIfNullCollectionShouldBeJoined() {
        TextUtils fixture = new TextUtils();

        String result = fixture.getJoined((String[]) null, ' ');

        assertEquals("", result);
    }

    @Test
    public void shouldReturnEmptyTextIfInputCollectionIsEmpty() {
        TextUtils fixture = new TextUtils();

        String result = fixture.getJoined(ListUtils.EMPTY_LIST, ' ');

        assertEquals("", result);
    }

    @Test
    public void shouldReturnEmptyTextIfInputArrayIsEmpty() {
        TextUtils fixture = new TextUtils();

        String result = fixture.getJoined(new String[]{}, ' ');

        assertEquals("", result);
    }

    @Test
    public void shouldJoinTextsFromCollectionWithSpecifiedSeparator() {
        TextUtils fixture = new TextUtils();
        Collection<String> texts = new ArrayList<String>(3);
        texts.add("A");
        texts.add("B");
        texts.add("C");

        String result = fixture.getJoined(texts, ',');

        assertEquals("A,B,C", result);
    }

    @Test
    public void shouldJoinTextsFromArrayWithSpecifiedSeparator() {
        TextUtils fixture = new TextUtils();

        String result = fixture.getJoined(new String[]{"A", "B", "C"}, ',');

        assertEquals("A,B,C", result);
    }
}
