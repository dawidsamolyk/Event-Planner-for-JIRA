package edu.uz.jira.event.planner.util.text;

import edu.uz.jira.event.planner.database.active.objects.model.NamedEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Extracts names from Named Entities.
 */
public class EntityNameExtractor {

    /**
     * @param entities Named Entities.
     * @return Array of input Entities names.
     */
    public static String[] getNames(final NamedEntity[] entities) {
        if (entities == null || entities.length == 0) {
            return new String[]{};
        }
        List<String> result = new ArrayList<String>();

        for (NamedEntity each : entities) {
            String name = each.getName();
            if (name != null) {
                result.add(name);
            }
        }
        return result.toArray(new String[result.size()]);
    }
}
