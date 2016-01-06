function TimeLineTasksCreator() {
    this.taskGadgetCreator = new TaskGadgetCreator();
    this.dateUtil = new DateUtil();

    this.getElementById = function(id) {
        return document.getElementById(id);
    };

    this.getTasksToDoRow = function() {
        return this.getElementById('tasks-todo');
    };

    this.getDoneTasksRow = function() {
        return this.getElementById('done-tasks');
    };

    this.createLateTaskCell = function() {
        var lateCell = this.createTaskCell(0);
        lateCell.style.background = '#d04437';
        var lateDoneCell = this.createDoneTaskCell(0);
        lateDoneCell.style.background = '#d04437';

        return lateCell;
    };

    this.fillLateCellByIssues = function(lateCell, issues) {
        for(eachIssue in issues) {
            var issue = issues[eachIssue];

            if(issue.daysAwayFromDueDate < 0 && issue.done === false) {
                var lateTaskGadget = this.taskGadgetCreator.createLate(issue);
                lateCell.appendChild(lateTaskGadget);
            }
        }
    };

    this.createTasksCells = function(weekDaysDates, deadlineDate) {
        var result = {};
        var today = new Date();
        var currentCellIndex = this.getTasksToDoRow().cells.length;

        var cell, doneCell;
        for(eachDate in weekDaysDates) {
            var date = weekDaysDates[eachDate];

            if(this.dateUtil.isTheSameDay(date, today)) {
                cell = this.createTodayTaskCell(currentCellIndex);
                doneCell = this.createTodayDoneTaskCell(currentCellIndex);
            } else {
                cell = this.createTaskCell(currentCellIndex);
                doneCell = this.createDoneTaskCell(currentCellIndex);
            }

            if(this.dateUtil.isTheSameDay(date, deadlineDate)) {
                this.setAsDeadlineTaskCellAtIndex(currentCellIndex);
            }

            result[date] = { 'toDo' : cell, 'done' : doneCell };

            currentCellIndex++;
        }
        return result;
    };

    this.fillCellsByIssues = function(cells, issues) {
        for(eachIssue in issues) {
            var issue = issues[eachIssue];
            var issueDueDate = new Date(issue.dueDate);

            for(eachDate in cells) {
                if(this.dateUtil.isTheSameDay(issueDueDate, new Date(eachDate))) {
                    var cell = cells[eachDate];

                    if(issue.done === true) {
                        cell.done.appendChild(this.taskGadgetCreator.createDone(issue));
                    } else {
                        cell.toDo.appendChild(this.taskGadgetCreator.createToDo(issue));
                    }
                }
            }
        }
    };

    this.createTaskCell = function(index) {
        var result = this.getTasksToDoRow().insertCell(index);
        result.style.verticalAlign = 'bottom';
        result.style.padding = 0;
        return result;
    };

    this.createTodayTaskCell = function(index) {
        var result = this.createTaskCell(index);
        result.style.background = '#f5f5f5';
        result.style.borderLeft = '3px solid #205081';
        result.style.borderRight = '3px solid #205081';
        return result;
    };

    this.createDoneTaskCell = function(index) {
        var result = this.getDoneTasksRow().insertCell(index);
        result.style.verticalAlign = 'bottom';
        result.style.padding = 0;
        result.style.textDecoration = 'line-through';
        return result;
    };

    this.createTodayDoneTaskCell = function(index) {
        var result = this.createDoneTaskCell(index);
        result.style.background = '#f5f5f5';
        result.style.borderLeft = '3px solid #205081';
        result.style.borderRight = '3px solid #205081';
        return result;
    };

    this.setAsDeadlineTaskCellAtIndex = function(index) {
        var toDoTasksRow = this.getTasksToDoRow();
        this.setDeadlineStyle(toDoTasksRow.cells[index]);

        var doneTasksRow = this.getDoneTasksRow();
        this.setDeadlineStyle(doneTasksRow.cells[index]);
    };

    this.setDeadlineStyle = function(cell) {
        cell.style.borderLeft = '3px solid #14892c';
        cell.style.borderRight = '3px solid #14892c';
        cell.style.background = '#f5f5f5';
    };
};