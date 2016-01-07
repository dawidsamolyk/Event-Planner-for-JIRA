function DateUtil() {
    var that = this;

    that.getDaysDifference = function(firstDate, secondDate) {
        var timeDifference = firstDate.getTime() - secondDate.getTime();
        return Math.ceil(timeDifference / (1000 * 3600 * 24));
    };

    that.isTheSameDay = function(firstDate, secondDate) {
        return firstDate != undefined && secondDate != undefined
                && firstDate.getFullYear() === secondDate.getFullYear()
                && firstDate.getMonth() === secondDate.getMonth()
                && firstDate.getDate() === secondDate.getDate();
    };

    that.isBeforeToday = function(date) {
        var today = new Date();
        return that.isDateBeforeDate(date, today);
    };

    that.isDateBeforeOrSameDate = function(firstDate, secondDate) {
        return firstDate <= secondDate || that.isTheSameDay(firstDate, secondDate);
    };

    that.isDateAfterOrSameDate = function(firstDate, secondDate) {
        return firstDate >= secondDate || that.isTheSameDay(firstDate, secondDate);
    };

    that.setNextDayAndGetDateString = function(date) {
        that.addDaysToDate(date, 1);
        return date.toDateString();
    };

    that.addDaysToDate = function(date, numberOfDays) {
        date.setDate(date.getDate() + numberOfDays);
    };

    that.getDateWithAddedDays = function(date, numberOfDays) {
        var result = new Date(date);
        result.setDate(date.getDate() + numberOfDays);
        return result;
    };

    that.substractDaysFromDate = function(date, numberOfDays) {
        date.setDate(date.getDate() - numberOfDays);
    };

    that.getDateWithSubstractedDays = function(date, numberOfDays) {
        var result = new Date(date);
        result.setDate(date.getDate() - numberOfDays);
        return result;
    };
};