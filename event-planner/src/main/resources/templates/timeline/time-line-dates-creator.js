function TimeLineDatesCreator() {
    var that = this;
    that.dateUtil = new DateUtil();
    that.todayLabel = '<br>Today';
    that.deadlineLabel = '<br>Deadline';
    that.todayDeadlineLabel = '<br>Deadline is today!';

    that.createLateDateCell = function(maximumIssueLate) {
        if(maximumIssueLate != 0) {
            that.createLateDateCell(maximumIssueLate);
        }
    };

    that.createCells = function(weekDaysDates, deadlineDate) {
        var today = new Date();
        var currentCellIndex = that.getDatesRow().cells.length;

        for(eachDate in weekDaysDates) {
            var date = weekDaysDates[eachDate];

            if(that.dateUtil.isTheSameDay(date, today)) {
                that.createTodayDateCell(currentCellIndex);
            } else {
                that.createDateCell(currentCellIndex, date.toDateString());
            }

            if(that.dateUtil.isTheSameDay(date, deadlineDate)) {
                that.setAsDeadlineDateCellAtIndex(currentCellIndex);
            }

            currentCellIndex++;
        }
    };

    that.createDateCell = function(index, label) {
        var result = that.getDatesRow().insertCell(index);
        result.style.textAlign = 'center';
        result.innerHTML = label;
        return result;
    };

    that.getDatesRow = function() {
        return document.getElementById('dates');
    };

    that.createLateDateCell = function(numberOfLateDays) {
        var result;
        switch(numberOfLateDays) {
            case 1: result = that.createDateCell(0, "1 day late"); break;
            default: result = that.createDateCell(0, numberOfLateDays + " days late"); break;
        }
        result.style.color = '#d04437';
        return result;
    };

    that.createTodayDateCell = function(index) {
        var today = new Date();
        var result = that.createDateCell(index, today.toDateString() + that.todayLabel);
        result.style.borderLeft = '3px solid #205081';
        result.style.borderRight = '3px solid #205081';
        result.style.color = '#205081';
        return result;
    };

    that.setAsDeadlineDateCellAtIndex = function(index) {
        var datesTable = that.getDatesRow();
        var cell = datesTable.cells[index];
        cell.style.borderLeft = '3px solid #14892c';
        cell.style.borderRight = '3px solid #14892c';
        cell.style.color = '#14892c';

        if(cell.innerHTML.includes(that.todayLabel)) {
            cell.innerHTML = cell.innerHTML.replace(that.todayLabel, that.todayDeadlineLabel);
        } else {
            cell.innerHTML += that.deadlineLabel;
        }

        return cell;
    };
};