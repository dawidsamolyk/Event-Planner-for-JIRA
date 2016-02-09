package edu.uz.jira.event.planner.util;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.security.JiraAuthenticationContext;
import edu.uz.jira.event.planner.exception.NullArgumentException;

import javax.annotation.Nonnull;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Calculates difference between dates.
 */
public class DatesDifferenceCalculator {
    public static final int MILLIS_IN_DAY = 24 * 60 * 60 * 1000;
    private final JiraAuthenticationContext authenticationContext;

    /**
     * Constructor.
     */
    public DatesDifferenceCalculator() {
        authenticationContext = ComponentAccessor.getJiraAuthenticationContext();
    }

    /**
     * @param current Current date (earlier).
     * @param newer   Newer date (older).
     * @return Dates difference.
     * @throws NullArgumentException Thrown when any of input argument is null.
     */
    public DatesDifference calculate(final Date current, final Date newer) throws NullArgumentException {
        if (current == null || newer == null) {
            throw new NullArgumentException(Date.class.getName());
        }
        Calendar currentDateCalendar = getCalendar(current);
        Calendar newerDateCalendar = getCalendar(newer);

        int monthsDifference = getMonthsDifference(newerDateCalendar, currentDateCalendar);
        addMonth(newerDateCalendar, -monthsDifference);

        int daysDifference = getDifference(Calendar.DAY_OF_YEAR, newerDateCalendar, currentDateCalendar);

        if (daysDifference < 0) {
            monthsDifference--;
            addMonth(newerDateCalendar, 1);
            daysDifference = Math.abs(getDaysDifference(newerDateCalendar.getTime(), currentDateCalendar.getTime()));
        }

        return new DatesDifference(monthsDifference, daysDifference);
    }

    private Calendar addMonth(@Nonnull final Calendar newerDateCalendar, final int amount) {
        newerDateCalendar.add(Calendar.MONTH, amount);
        return newerDateCalendar;
    }

    private int getDifference(int field, @Nonnull final Calendar newerDateCalendar, @Nonnull final Calendar currentDateCalendar) {
        return newerDateCalendar.get(field) - currentDateCalendar.get(field);
    }

    private int getMonthsDifference(@Nonnull final Calendar newerDateCalendar, @Nonnull final Calendar currentDateCalendar) {
        int yearsDifference = getDifference(Calendar.YEAR, newerDateCalendar, currentDateCalendar);
        return (yearsDifference * 12) + getDifference(Calendar.MONTH, newerDateCalendar, currentDateCalendar);
    }

    private Calendar getCalendar(@Nonnull final Date date) {
        Locale locale = authenticationContext.getLocale();
        Calendar result = Calendar.getInstance(locale);
        result.setTime(date);
        return result;
    }

    static public int getDaysDifference(final Date current, final Date newer) {
        if (current == null || newer == null) {
            return 0;
        }
        int currentTimeInDays = (int) (current.getTime() / MILLIS_IN_DAY);
        int dueDateTimeInDays = (int) (newer.getTime() / MILLIS_IN_DAY);
        return dueDateTimeInDays - currentTimeInDays;
    }
}
