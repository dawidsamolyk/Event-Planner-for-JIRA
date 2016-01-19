function TimeLineDatesCreator() {
    "use strict";
    var that = this;
    that.dateUtil = new DateUtil();
    that.todayLabel = '<br>Today';
    that.deadlineLabel = '<br>Deadline';
    that.todayDeadlineLabel = '<br>Deadline is today!';

    that.createLateDateCell = function (maximumIssueLate) {
        if (maximumIssueLate !== 0) {
            that.createLateDateCell(maximumIssueLate);
        }
    };

    that.createCells = function (weekDaysDates, deadlineDate) {
        var today, currentCellIndex, eachDate, date;
        today = new Date();
        currentCellIndex = that.getDatesRow().cells.length;

        for (eachDate in weekDaysDates) {
            date = weekDaysDates[eachDate];

            if (that.dateUtil.isTheSameDay(date, today)) {
                that.createTodayDateCell(currentCellIndex);
            } else {
                that.createDateCell(currentCellIndex, date.toDateString());
            }

            if (that.dateUtil.isTheSameDay(date, deadlineDate)) {
                that.setAsDeadlineDateCellAtIndex(currentCellIndex);
            }

            currentCellIndex += 1;
        }
    };

    that.createDateCell = function (index, label) {
        var result = that.getDatesRow().insertCell(index);
        result.style.textAlign = 'center';
        result.style.verticalAlign = 'middle';
        result.style.borderLeft = '1px solid #cccccc';
        result.style.borderRight = '1px solid #cccccc';
        result.innerHTML = label;
        return result;
    };

    that.getDatesRow = function () {
        return document.getElementById('dates');
    };

    that.createLateDateCell = function (numberOfLateDays) {
        var result;
        switch (numberOfLateDays) {
            case 1:
                result = that.createDateCell(0, "1 day late");
                break;
            default:
                result = that.createDateCell(0, numberOfLateDays + " days late");
                break;
        }
        result.style.color = '#d04437';
        return result;
    };

    that.createTodayDateCell = function (index) {
        var result = that.createDateCell(index, new Date().toDateString() + that.todayLabel);
        result.style.borderLeft = '3px solid #205081';
        result.style.borderRight = '3px solid #205081';
        result.style.color = '#205081';
        return result;
    };

    that.setAsDeadlineDateCellAtIndex = function (index) {
        var result = that.getDatesRow().cells[index];
        result.style.borderLeft = '3px solid #14892c';
        result.style.borderRight = '3px solid #14892c';
        result.style.color = '#14892c';

        if (result.innerHTML.includes(that.todayLabel)) {
            result.innerHTML = result.innerHTML.replace(that.todayLabel, that.todayDeadlineLabel);
        } else {
            result.innerHTML += that.deadlineLabel;
        }

        return result;
    };
};