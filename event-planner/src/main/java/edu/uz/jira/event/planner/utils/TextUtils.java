package edu.uz.jira.event.planner.utils;

import org.apache.commons.lang.StringUtils;

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
    public String getJoined(final Collection<String> collection, final String separator) {
        if (collection == null || collection.isEmpty()) {
            return new String("");
        }
        return StringUtils.join(collection, separator);
    }
}
