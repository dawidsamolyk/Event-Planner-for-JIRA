function TimeLineDatesCreator() {
    this.dateUtil = new DateUtil();

    this.createLateDateCell = function(maximumIssueLate) {
        if(maximumIssueLate != 0) {
            this.createLateDateCell(maximumIssueLate);
        }
    };

    this.createCells = function(weekDaysDates, deadlineDate) {
        var today = new Date();
        var currentCellIndex = this.getDatesRow().cells.length;

        for(eachDate in weekDaysDates) {
            var date = weekDaysDates[eachDate];

            if(this.dateUtil.isTheSameDay(date, today)) {
                this.createTodayDateCell(currentCellIndex);
            } else {
                this.createDateCell(currentCellIndex, date);
            }

            if(this.dateUtil.isTheSameDay(date, deadlineDate)) {
                this.setAsDeadlineDateCellAtIndex(currentCellIndex);
            }

            currentCellIndex++;
        }
    };

    this.createDateCell = function(index, date) {
        var result = this.getDatesRow().insertCell(index);
        result.style.textAlign = 'center';
        result.appendChild(document.createTextNode(date));
        return result;
    };

    this.getDatesRow = function() {
        return document.getElementById('dates');
    };

    this.createLateDateCell = function(numberOfLateDays) {
        var result;
        switch(numberOfLateDays) {
            case 1: result = this.createDateCell(0, "1 day late"); break;
            default: result = this.createDateCell(0, numberOfLateDays + " days late"); break;
        }
        result.style.color = '#d04437';
        return result;
    };

    this.createTodayDateCell = function(index) {
        var today = new Date();
        var result = this.createDateCell(index, today);
        result.style.borderLeft = '3px solid #205081';
        result.style.borderRight = '3px solid #205081';
        result.style.color = '#205081';
        return result;
    };

    this.setAsDeadlineDateCellAtIndex = function(index) {
        var datesTable = this.getDatesRow();
        var cell = datesTable.cells[index];
        cell.style.borderLeft = '3px solid #14892c';
        cell.style.borderRight = '3px solid #14892c';
        cell.style.color = '#14892c';
        return cell;
    };
};