function DateUtil() {
    this.getDaysDifference = function(firstDate, secondDate) {
        var timeDifference = firstDate.getTime() - secondDate.getTime();
        return Math.ceil(timeDifference / (1000 * 3600 * 24));
    };

    this.isTheSameDay = function(firstDate, secondDate) {
        return firstDate != undefined && secondDate != undefined
                && firstDate.getFullYear() === secondDate.getFullYear()
                && firstDate.getMonth() === secondDate.getMonth()
                && firstDate.getDate() === secondDate.getDate();
    };

    this.isBeforeToday = function(date) {
        var today = new Date();
        return this.isDateBeforeDate(date, today);
    };

    this.isDateBeforeOrSameDate = function(firstDate, secondDate) {
        return firstDate <= secondDate || this.isTheSameDay(firstDate, secondDate);
    };

    this.isDateAfterOrSameDate = function(firstDate, secondDate) {
        return firstDate >= secondDate || this.isTheSameDay(firstDate, secondDate);
    };

    this.setNextDayAndGetDateString = function(date) {
        this.addDaysToDate(date, 1);
        return date.toDateString();
    };

    this.addDaysToDate = function(date, numberOfDays) {
        date.setDate(date.getDate() + numberOfDays);
    };

    this.getDateWithAddedDays = function(date, numberOfDays) {
        var result = new Date(date);
        result.setDate(date.getDate() + numberOfDays);
        return result;
    };

    this.substractDaysFromDate = function(date, numberOfDays) {
        date.setDate(date.getDate() - numberOfDays);
    };

    this.getDateWithSubstractedDays = function(date, numberOfDays) {
        var result = new Date(date);
        result.setDate(date.getDate() - numberOfDays);
        return result;
    };
};