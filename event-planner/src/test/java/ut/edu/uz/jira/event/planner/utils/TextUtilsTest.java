package ut.edu.uz.jira.event.planner.utils;

import edu.uz.jira.event.planner.utils.TextUtils;
import org.apache.commons.collections.ListUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class TextUtilsTest {

    @Test
    public void shouldReturnEmptyTextIfNullCollectionShouldBeJoined() {
        TextUtils fixture = new TextUtils();

        String result = fixture.getJoined(null, " ");

        assertEquals("", result);
    }

    @Test
    public void shouldReturnEmptyTextIfInputCollectionIsEmpty() {
        TextUtils fixture = new TextUtils();

        String result = fixture.getJoined(ListUtils.EMPTY_LIST, " ");

        assertEquals("", result);
    }

    @Test
    public void separatorShouldNotBeUsedInTextJoiningIfItIsNull() {
        TextUtils fixture = new TextUtils();
        Collection<String> texts = new ArrayList<String>(3);
        texts.add("A");
        texts.add("B");
        texts.add("C");

        String result = fixture.getJoined(texts, null);

        assertEquals("ABC", result);
    }

    @Test
    public void shouldJoinTextWithSpecifiedSeparator() {
        TextUtils fixture = new TextUtils();
        Collection<String> texts = new ArrayList<String>(3);
        texts.add("A");
        texts.add("B");
        texts.add("C");

        String result = fixture.getJoined(texts, ",");

        assertEquals("A,B,C", result);
    }
}
