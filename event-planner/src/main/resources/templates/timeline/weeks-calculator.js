function WeeksCalculator() {
    "use strict";
    var that = this;
    that.currentWeekIndex = 0;
    that.dateUtil = new DateUtil();

    that.getWeeks = function (minDate, maxDate) {
        var result, todayDate, lastDayOfCurrentWeek;
        result = {};
        todayDate = new Date();

        result[that.currentWeekIndex] = that.createCurrentWeekDates(todayDate);

        that.createPreviousWeeksDates(todayDate, minDate, result);

        lastDayOfCurrentWeek = result[that.currentWeekIndex][6];
        that.createNextWeeksDates(lastDayOfCurrentWeek, maxDate, result);

        return result;
    };

    that.createCurrentWeekDates = function (todayDate) {
        var result, index;
        result = {};
        for (index = 0; index < 7; index += 1) {
            result[index] = that.dateUtil.getDateWithAddedDays(todayDate, index);
        }
        return result;
    };

    that.createPreviousWeeksDates = function (todayDate, minDate, array) {
        var firstPreviousDate, previousWeekIndex, index;
        firstPreviousDate = todayDate;
        previousWeekIndex = -1;

        while (that.dateUtil.isDateBeforeOrSameDate(minDate, firstPreviousDate)) {
            firstPreviousDate = that.dateUtil.getDateWithSubstractedDays(firstPreviousDate, 7);

            array[previousWeekIndex] = {};
            for (index = 0; index < 7; index += 1) {
                array[previousWeekIndex][index] = that.dateUtil.getDateWithAddedDays(firstPreviousDate, index);
            }
            previousWeekIndex -= 1;
        }
    };

    that.createNextWeeksDates = function (lastDayOfCurrentWeek, maxDate, array) {
        var firstNextDate, nextWeekIndex, index;
        firstNextDate = that.dateUtil.getDateWithAddedDays(lastDayOfCurrentWeek, 1);
        nextWeekIndex = 1;

        while (that.dateUtil.isDateAfterOrSameDate(maxDate, firstNextDate)) {
            array[nextWeekIndex] = {};
            for (index = 0; index < 7; index += 1) {
                array[nextWeekIndex][index] = that.dateUtil.getDateWithAddedDays(firstNextDate, index);
            }
            nextWeekIndex++;

            firstNextDate = that.dateUtil.getDateWithAddedDays(firstNextDate, 7);
        }
    };
};