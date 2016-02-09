package edu.uz.jira.event.planner.util;

/**
 * Representation of difference between dates.
 */
public class DatesDifference {
    private int months;
    private int days;

    public DatesDifference(int months, int days) {
        this.months = months;
        this.days = days;
    }

    public int getMonths() {
        return months;
    }

    public int getDays() {
        return days;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DatesDifference that = (DatesDifference) o;

        if (months != that.months) return false;
        return days == that.days;
    }

    @Override
    public int hashCode() {
        int result = months;
        result = 31 * result + days;
        return result;
    }
}
