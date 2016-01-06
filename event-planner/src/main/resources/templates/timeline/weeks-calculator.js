function WeeksCalculator() {
    this.currentWeekIndex = 0;
    this.dateUtil = new DateUtil();

    this.getWeeks = function(minDate, maxDate) {
        var result = {};
        var todayDate = new Date();

        result[this.currentWeekIndex] = this.createCurrentWeekDates(todayDate);

        this.createPreviousWeeksDates(todayDate, minDate, result);

        var lastDayOfCurrentWeek = result[this.currentWeekIndex][6];
        this.createNextWeeksDates(lastDayOfCurrentWeek, maxDate, result);

        return result;
    };

    this.createCurrentWeekDates = function(todayDate) {
        result = {};
        for(index = 0; index < 7; index++) {
            result[index] = this.dateUtil.getDateWithAddedDays(todayDate, index);
        }
        return result;
    };

    this.createPreviousWeeksDates = function(todayDate, minDate, array) {
        var firstPreviousDate = todayDate;
        var previousWeekIndex = -1;

        while(this.dateUtil.isDateBeforeOrSameDate(minDate, firstPreviousDate)) {
            firstPreviousDate = this.dateUtil.getDateWithSubstractedDays(firstPreviousDate, 7);

            array[previousWeekIndex] = {};
            for(index = 0; index < 7; index++) {
                array[previousWeekIndex][index] = this.dateUtil.getDateWithAddedDays(firstPreviousDate, index);
            }
            previousWeekIndex--;
        }
    };

    this.createNextWeeksDates = function(lastDayOfCurrentWeek, maxDate, array) {
        var firstNextDate = this.dateUtil.getDateWithAddedDays(lastDayOfCurrentWeek, 1);
        var nextWeekIndex = 1;

        while(this.dateUtil.isDateAfterOrSameDate(maxDate, firstNextDate)) {
            array[nextWeekIndex] = {};
            for(index = 0; index < 7; index++) {
                array[nextWeekIndex][index] = this.dateUtil.getDateWithAddedDays(firstNextDate, index);
            }
            nextWeekIndex++;

            firstNextDate = this.dateUtil.getDateWithAddedDays(firstNextDate, 7);
        }
    };
};