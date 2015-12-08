package edu.uz.jira.event.planner.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * Helpers for doing operations on texts.
 */
public class TextUtils {

    /**
     * @param collection Collection of texts to join.
     * @param separator  Separator which joins texts each other.
     * @return Joined text.
     */
    public String getJoined(final Collection<String> collection, final char separator) {
        if (collection == null || collection.isEmpty()) {
            return new String("");
        }
        return StringUtils.join(collection, separator);
    }

    /**
     * @param array     Array of texts to join.
     * @param separator Separator which joins texts each other.
     * @return Joined text.
     */
    public String getJoined(final String[] array, final char separator) {
        if (array == null || array.length == 0) {
            return new String("");
        }
        return getJoined(Arrays.asList(array), separator);
    }
}
