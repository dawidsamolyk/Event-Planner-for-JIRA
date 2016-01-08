package edu.uz.jira.event.planner.database.active.objects;

import com.atlassian.activeobjects.external.ActiveObjects;
import net.java.ao.Query;
import net.java.ao.RawEntity;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helpers for working with Active Objects.
 */
class ActiveObjectsHelper {
    private final ActiveObjects activeObjectsService;

    ActiveObjectsHelper(@Nonnull final ActiveObjects activeObjectsService) {
        this.activeObjectsService = activeObjectsService;
    }

    <T extends RawEntity<K>, K> List<T> get(@Nonnull final Class<T> type, @Nonnull final String whereClause, @Nonnull final String[] whereValues) {
        List<T> result = new ArrayList<T>();
        if (whereValues != null && whereValues.length > 0 && whereClause != null) {
            for (String eachName : whereValues) {
                T[] eachComponent = activeObjectsService.find(type, Query.select().where(whereClause, eachName));
                result.addAll(Arrays.asList(eachComponent));
            }
        }
        return result;
    }
}
