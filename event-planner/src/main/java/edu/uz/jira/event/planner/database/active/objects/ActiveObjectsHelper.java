package edu.uz.jira.event.planner.database.active.objects;

import com.atlassian.activeobjects.external.ActiveObjects;
import edu.uz.jira.event.planner.database.active.objects.model.Component;
import edu.uz.jira.event.planner.database.active.objects.model.Plan;
import edu.uz.jira.event.planner.database.active.objects.model.Task;
import net.java.ao.Query;
import net.java.ao.RawEntity;

import javax.annotation.Nonnull;
import java.util.*;

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

    void updateEstimatedDaysToComplete(@Nonnull final Plan plan) {
        int estimatedDaysToCompletePlan = updateNeededDaysToComplete(plan);

        Set<Integer> neededDays = new HashSet<Integer>();
        int neededExtraDays = 0;

        for (Component each : plan.getComponents()) {
            for (Task eachTask : each.getTasks()) {
                int eachTaskNeededDays = (eachTask.getNeededMonthsToComplete() * 30) + eachTask.getNeededDaysToComplete();

                if (neededDays.contains(eachTaskNeededDays)) {
                    neededExtraDays++;
                } else {
                    neededDays.add(eachTaskNeededDays);
                }
            }
        }

        plan.setTheWorstTimeToComplete(estimatedDaysToCompletePlan + neededExtraDays);
    }

    private int updateNeededDaysToComplete(@Nonnull final Plan plan) {
        int maximumDays = 0;
        for (Component each : plan.getComponents()) {
            int eachComponentMaxDays = getMaximumDaysToComplete(each);
            if (eachComponentMaxDays > maximumDays) {
                maximumDays = eachComponentMaxDays;
            }
        }

        int estimatedDaysToCompletePlan = maximumDays + plan.getReserveTimeInDays();
        plan.setOptimisticTimeToComplete(estimatedDaysToCompletePlan);

        return estimatedDaysToCompletePlan;
    }

    private int getMaximumDaysToComplete(@Nonnull final Component component) {
        int maximumNeededDays = 0;

        for (Task eachTask : component.getTasks()) {
            int eachTaskNeededDays = (eachTask.getNeededMonthsToComplete() * 30) + eachTask.getNeededDaysToComplete();
            if (eachTaskNeededDays > maximumNeededDays) {
                maximumNeededDays = eachTaskNeededDays;
            }
        }
        return maximumNeededDays;
    }
}
