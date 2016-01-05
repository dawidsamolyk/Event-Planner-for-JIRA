function DateUtil() {
    this.isTheSameDay = function(firstDate, secondDate) {
        return firstDate != undefined && secondDate != undefined
                && firstDate.getFullYear() === secondDate.getFullYear()
                && firstDate.getMonth() === secondDate.getMonth()
                && firstDate.getDate() === secondDate.getDate();
    };

    this.setNextDayAndGetDateString = function(date) {
        date.setDate(date.getDate() + 1);
        return date.toDateString();
    };
};