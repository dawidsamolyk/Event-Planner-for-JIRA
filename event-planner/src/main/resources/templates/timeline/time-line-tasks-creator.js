function TimeLineTasksCreator() {
    "use strict";
    var that = this;
    that.taskGadgetCreator = new TaskGadgetCreator();
    that.dateUtil = new DateUtil();
    that.tasksToggle = new TasksToggle();

    that.getElementById = function (id) {
        return document.getElementById(id);
    };

    that.getTasksToDoRow = function () {
        return that.getElementById('tasks-todo');
    };

    that.getDoneTasksRow = function () {
        return that.getElementById('done-tasks');
    };

    that.createLateTaskCells = function () {
        var lateCell, lateDoneCell, result = {};

        lateCell = that.createTaskCell(0);
        lateCell.style.background = '#d04437';

        lateDoneCell = that.createDoneTaskCell(0);
        lateDoneCell.style.background = '#d04437';

        result['late'] = {'toDo': lateCell, 'done': lateDoneCell};

        return result;
    };

    that.fillLateCellByIssues = function (lateCells, issues) {
        var eachIssue, issue, lateTaskGadget, cellList, lateToDoCell;
        lateToDoCell = lateCells['late'].toDo;
        cellList = that.getListFor(lateToDoCell);

        for (eachIssue in issues) {
            issue = issues[eachIssue];

            if (issue.daysAwayFromDueDate < 0 && issue.status !== 'done') {
                lateTaskGadget = that.taskGadgetCreator.createLate(issue);
                cellList.appendChild(lateTaskGadget);
            }
        }

        that.addScrollButtonIfRequired(lateToDoCell, cellList);
    };

    that.addScrollButtonIfRequired = function (cell, list) {
        if (that.tasksToggle.addIfRequired(cell, list.id)) {
            list.style.maxHeight = '200px';
            cell.style.maxHeight = '230px';
            list.style.overflow = 'hidden';
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
        var eachIssue, issue, issueDueDate, eachDate, cell, cellList, issueStatus;
        for (eachIssue in issues) {
            issue = issues[eachIssue];
            issueDueDate = new Date(issue.dueDate);
            issueStatus = issue.status;

            for (eachDate in cells) {
                if (that.dateUtil.isTheSameDay(issueDueDate, new Date(eachDate))) {
                    cell = cells[eachDate];

                    if (issueStatus === 'done') {
                        cellList = that.getListFor(cell.done);
                        cellList.appendChild(that.taskGadgetCreator.createDone(issue));
                    } else if (issueStatus === 'indeterminate') {
                        cellList = that.getListFor(cell.toDo);
                        cellList.appendChild(that.taskGadgetCreator.createInProgress(issue));
                    } else {
                        cellList = that.getListFor(cell.toDo);
                        cellList.appendChild(that.taskGadgetCreator.createNew(issue));
                    }

                    if (issueStatus !== 'done') {
                        that.addScrollButtonIfRequired(cell.toDo, cellList);
                    }
                }
            }
        }
    };

    that.getListFor = function (parent) {
        var index;
        for (index = 0; index < parent.childNodes.length; index += 1) {
            if (parent.childNodes[index].className === 'connectedSortable') {
                return parent.childNodes[index];
            }
        }
    };

    that.createTaskCell = function (index) {
        var result = that.getTasksToDoRow().insertCell(index);
        result.style.verticalAlign = 'bottom';
        result.style.padding = 0;
        result.style.borderLeft = '1px solid #cccccc';
        result.style.borderRight = '1px solid #cccccc';
        result.style.borderTop = 'none';
        result.style.height = '230px';
        result.id = 'new-' + index;

        that.appendListRoot(result);

        return result;
    };

    that.appendListRoot = function (parent) {
        var result = document.createElement('UL');
        result.className = 'connectedSortable';
        result.style.listStyleType = 'none';
        result.style.listStyle = 'none';
        result.style.margin = '0';
        result.style.padding = '5px 0 0 0';
        result.style.verticalAlign = 'bottom';
        result.style.height = '100%';
        result.id = 'list-' + parent.id;

        parent.appendChild(result);
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
        result.style.verticalAlign = 'top';
        result.style.padding = 0;
        result.style.borderLeft = '1px solid #cccccc';
        result.style.borderRight = '1px solid #cccccc';
        result.style.borderBottom = 'none';
        result.style.textDecoration = 'line-through';
        result.id = 'done-' + index;

        that.appendListRoot(result);

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