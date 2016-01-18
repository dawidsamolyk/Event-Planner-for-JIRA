function TimeLineTasksCreator() {
    "use strict";
    var that = this;
    that.taskGadgetCreator = new TaskGadgetCreator();
    that.dateUtil = new DateUtil();

    that.getElementById = function (id) {
        return document.getElementById(id);
    };

    that.getTasksToDoRow = function () {
        return that.getElementById('tasks-todo');
    };

    that.getDoneTasksRow = function () {
        return that.getElementById('done-tasks');
    };

    that.createLateTaskCell = function () {
        var lateCell, lateDoneCell;

        lateCell = that.createTaskCell(0);
        lateCell.style.background = '#d04437';

        lateDoneCell = that.createDoneTaskCell(0);
        lateDoneCell.style.background = '#d04437';

        return lateCell;
    };

    that.fillLateCellByIssues = function (lateCell, issues) {
        var eachIssue, issue, lateTaskGadget;

        for (eachIssue in issues) {
            issue = issues[eachIssue];

            if (issue.daysAwayFromDueDate < 0 && issue.done === false) {
                lateTaskGadget = that.taskGadgetCreator.createLate(issue);
                lateCell.appendChild(lateTaskGadget);
            }
        }
    };

    that.createTasksCells = function (weekDaysDates, deadlineDate) {
        var result, today, currentCellIndex, cell, doneCell, eachDate, date;
        result = {};
        today = new Date();
        currentCellIndex = that.getTasksToDoRow().cells.length;

        for (eachDate in weekDaysDates) {
            date = weekDaysDates[eachDate];

            if (that.dateUtil.isTheSameDay(date, today)) {
                cell = that.createTodayTaskCell(currentCellIndex);
                doneCell = that.createTodayDoneTaskCell(currentCellIndex);
            } else {
                cell = that.createTaskCell(currentCellIndex);
                doneCell = that.createDoneTaskCell(currentCellIndex);
            }

            if (that.dateUtil.isTheSameDay(date, deadlineDate)) {
                that.setAsDeadlineTaskCellAtIndex(currentCellIndex);
            }

            result[date] = {'toDo': cell, 'done': doneCell};

            currentCellIndex += 1;
        }
        return result;
    };

    that.fillCellsByIssues = function (cells, issues) {
        var eachIssue, issue, issueDueDate, eachDate, cell;
        for (eachIssue in issues) {
            issue = issues[eachIssue];
            issueDueDate = new Date(issue.dueDate);

            for (eachDate in cells) {
                if (that.dateUtil.isTheSameDay(issueDueDate, new Date(eachDate))) {
                    cell = cells[eachDate];

                    if (issue.done === true) {
                        cell.done.appendChild(that.taskGadgetCreator.createDone(issue));
                    } else {
                        cell.toDo.appendChild(that.taskGadgetCreator.createToDo(issue));
                    }
                }
            }
        }
    };

    that.createTaskCell = function (index) {
        var result = that.getTasksToDoRow().insertCell(index);
        result.style.verticalAlign = 'bottom';
        result.style.padding = 0;
        result.style.borderLeft = '1px solid #cccccc';
        result.style.borderRight = '1px solid #cccccc';
        return result;
    };

    that.createTodayTaskCell = function (index) {
        var result = that.createTaskCell(index);
        result.style.background = '#f5f5f5';
        result.style.borderLeft = '3px solid #205081';
        result.style.borderRight = '3px solid #205081';
        return result;
    };

    that.createDoneTaskCell = function (index) {
        var result = that.getDoneTasksRow().insertCell(index);
        result.style.verticalAlign = 'bottom';
        result.style.padding = 0;
        result.style.borderLeft = '1px solid #cccccc';
        result.style.borderRight = '1px solid #cccccc';
        result.style.textDecoration = 'line-through';
        return result;
    };

    that.createTodayDoneTaskCell = function (index) {
        var result = that.createDoneTaskCell(index);
        result.style.background = '#f5f5f5';
        result.style.borderLeft = '3px solid #205081';
        result.style.borderRight = '3px solid #205081';
        return result;
    };

    that.setAsDeadlineTaskCellAtIndex = function (index) {
        var toDoTasksRow, doneTasksRow;

        toDoTasksRow = that.getTasksToDoRow();
        that.setDeadlineStyle(toDoTasksRow.cells[index]);

        doneTasksRow = that.getDoneTasksRow();
        that.setDeadlineStyle(doneTasksRow.cells[index]);
    };

    that.setDeadlineStyle = function (cell) {
        cell.style.borderLeft = '3px solid #14892c';
        cell.style.borderRight = '3px solid #14892c';
        cell.style.background = '#f5f5f5';
    };
};