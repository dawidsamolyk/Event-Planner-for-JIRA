package edu.uz.jira.event.planner.util.text;

import edu.uz.jira.event.planner.project.plan.model.NamedEntityWithDescription;

import java.util.Arrays;

/**
 * Extracts names from Named Entities.
 */
public class EntityNameExtractor {

    /**
     * @param entities Named Entities.
     * @return Array of input Entities names.
     */
    public String[] getNames(final NamedEntityWithDescription[] entities) {
        if (entities == null || entities.length == 0) {
            return new String[]{};
        }
        int numberOfEntities = entities.length;
        String[] result = new String[numberOfEntities];

        for (int index = 0; index < numberOfEntities; index++) {
            String name = entities[index].getName();
            if (name != null) {
                result[index] = new String(name);
            }
        }

        return Arrays.stream(result)
                .filter(s -> (s != null))
                .toArray(String[]::new);
    }
}
