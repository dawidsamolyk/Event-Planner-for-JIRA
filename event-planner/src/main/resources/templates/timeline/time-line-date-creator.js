function TimeLineDateCreator(datesId) {
    this.datesId = datesId;
    this.createdCells = {};
    this.getElementById = function(id) { return document.getElementById(id); };
    this.dateUtil = new DateUtil();

    this.createTodayDateCell = function(index, date) {
        var result = this.createDateCell(index, date);
        result.style.borderLeft = '3px solid #205081';
        result.style.borderRight = '3px solid #205081';
        result.style.color = '#205081';
        return result;
    };

    this.createLateDateCell = function(numberOfLateDays) {
        var result;

        if(numberOfLateDays === 0) {
            result = this.createDateCell(0, "No late");
        }
        else if(numberOfLateDays === -1) {
            result = this.createDateCell(0, "1 day late");
        }
        else {
            result = this.createDateCell(0, Math.abs(numberOfLateDays) + " days late");
        }
        result.style.color = '#d04437';
        return result;
    };

    this.createDateCell = function(index, date, showingWeekBeforeCurrent) {
        var dates = this.getElementById(this.datesId);
        var result = dates.insertCell(index);
        result.style.textAlign = 'center';
        result.appendChild(document.createTextNode(date));

        if(showingWeekBeforeCurrent === true) {
            result.style.color = '#d04437';
        }

        this.createdCells[date] = result;
        return result;
    };

    this.setDeadlineDate = function(deadlineDate) {
        for(eachDate in this.createdCells) {
            var eachCell = this.createdCells[eachDate];

            if(this.dateUtil.isTheSameDay(new Date(eachDate), deadlineDate)) {
                eachCell.style.borderLeft = '3px solid #14892c';
                eachCell.style.borderRight = '3px solid #14892c';
                eachCell.style.color = '#14892c';
                return eachCell.cellIndex;
            }
        }
        return -1;
    };
};