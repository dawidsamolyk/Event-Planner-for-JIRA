function DateUtil() {
    this.isTheSameDay = function(firstDate, secondDate) {
        return firstDate != undefined && secondDate != undefined
                && firstDate.getFullYear() === secondDate.getFullYear()
                && firstDate.getMonth() === secondDate.getMonth()
                && firstDate.getDate() === secondDate.getDate();
    };

    this.setNextDayAndGetDateString = function(date) {
        this.addDaysToDate(date, 1);
        return date.toDateString();
    };

    this.addDaysToDate = function(date, numberOfDays) {
        date.setDate(date.getDate() + numberOfDays);
    };

    this.substractDaysFromDate = function(date, numberOfDays) {
        date.setDate(date.getDate() - numberOfDays);
    };
};