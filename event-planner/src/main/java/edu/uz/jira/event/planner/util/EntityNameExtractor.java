package edu.uz.jira.event.planner.util;

import edu.uz.jira.event.planner.project.plan.model.NamedEntity;

import javax.annotation.Nonnull;

/**
 * Extracts names from Named Entities.
 */
public class EntityNameExtractor {

    public String[] getNames(@Nonnull final NamedEntity[] entities) {
        int numberOfEntities = entities.length;
        String[] result = new String[numberOfEntities];

        for (int index = 0; index < numberOfEntities; index++) {
            result[index] = new String(entities[index].getName());
        }

        return result;
    }
}
