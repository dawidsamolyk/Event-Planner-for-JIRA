function WeeksCalculator() {
    var that = this;
    that.currentWeekIndex = 0;
    that.dateUtil = new DateUtil();

    that.getWeeks = function(minDate, maxDate) {
        var result = {};
        var todayDate = new Date();

        result[that.currentWeekIndex] = that.createCurrentWeekDates(todayDate);

        that.createPreviousWeeksDates(todayDate, minDate, result);

        var lastDayOfCurrentWeek = result[that.currentWeekIndex][6];
        that.createNextWeeksDates(lastDayOfCurrentWeek, maxDate, result);

        return result;
    };

    that.createCurrentWeekDates = function(todayDate) {
        result = {};
        for(index = 0; index < 7; index++) {
            result[index] = that.dateUtil.getDateWithAddedDays(todayDate, index);
        }
        return result;
    };

    that.createPreviousWeeksDates = function(todayDate, minDate, array) {
        var firstPreviousDate = todayDate;
        var previousWeekIndex = -1;

        while(that.dateUtil.isDateBeforeOrSameDate(minDate, firstPreviousDate)) {
            firstPreviousDate = that.dateUtil.getDateWithSubstractedDays(firstPreviousDate, 7);

            array[previousWeekIndex] = {};
            for(index = 0; index < 7; index++) {
                array[previousWeekIndex][index] = that.dateUtil.getDateWithAddedDays(firstPreviousDate, index);
            }
            previousWeekIndex--;
        }
    };

    that.createNextWeeksDates = function(lastDayOfCurrentWeek, maxDate, array) {
        var firstNextDate = that.dateUtil.getDateWithAddedDays(lastDayOfCurrentWeek, 1);
        var nextWeekIndex = 1;

        while(that.dateUtil.isDateAfterOrSameDate(maxDate, firstNextDate)) {
            array[nextWeekIndex] = {};
            for(index = 0; index < 7; index++) {
                array[nextWeekIndex][index] = that.dateUtil.getDateWithAddedDays(firstNextDate, index);
            }
            nextWeekIndex++;

            firstNextDate = that.dateUtil.getDateWithAddedDays(firstNextDate, 7);
        }
    };
};