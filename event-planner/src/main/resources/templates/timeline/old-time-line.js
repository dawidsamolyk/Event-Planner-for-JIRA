function TimeLine() {
    id = 'time-line';
    tasksToDoId = 'tasks-todo';
    datesId = 'dates';
    doneTasksId = 'done-tasks';
    this.deadlineDate;
    this.deadlineDateIndex;
    this.timeLineStartDate;
    this.issues;
    datesCreator = new TimeLineDatesCreator();
    tasksCreator = new TimeLineTasksCreator(tasksToDoId, doneTasksId);
    taskGadgetCreator = new TaskGadgetCreator();
    dateUtil = new DateUtil();

    this.clear = function() {
        this.clearTable(tasksToDoId);
        this.clearTable(datesId);
        this.clearTable(doneTasksId);
    };

    this.clearTable = function(id) {
        var table = document.getElementById(id);
        while(table.cells.length > 0) {
            table.deleteCell(0);
        }
    };

    this.isViewingCurrentWeek = function() {
        return dateUtil.isTheSameDay(new Date(), this.timeLineStartDate);
    };

    this.isDeadlineToday = function() {
        datesCreator.deadlineDateCellIndex === 1;
    };

    this.refresh = function() {
        this.clear();

        var tasksCellsStartIndex = 0;
        if(this.isViewingCurrentWeek()) {
            var lateCell = tasksCreator.createLateTaskCell();
            tasksCreator.createLateDoneTaskCell();

            var todayTasksCell = tasksCreator.createTodayTaskCell();
            var todayDoneTasksCell = tasksCreator.createTodayDoneTaskCell();

            if(this.isDeadlineToday()) {
                todayCell.setAsDeadline();
                todayDoneCell.setAsDeadline();
            }

            tasksCellsStartIndex = 1;
        }

        var nextDaysCells = [];
        var nextDaysDoneCells = [];
        var numberOfNextDaysToShow = 7 - tasksCellsStartIndex;
        for(index = tasksCellsStartIndex; index < numberOfNextDaysToShow; index++) {
            nextDaysCells[index] = tasksCreator.createTaskCell(index + tasksCellsStartIndex);
            nextDaysDoneCells[index] = tasksCreator.createDoneTaskCell(index + tasksCellsStartIndex);

            if(datesCreator.deadlineDateCellIndex === index) {
                nextDaysCells[index].setAsDeadline();
                nextDaysDoneCells[index].setAsDeadline();
            }
        }

        var daysDifference = dateUtil.getDaysDifference(this.timeLineStartDate, new Date());

        var maximumLate = 0;

        for(eachIssueKey in this.issues) {
            var eachIssue = this.issues[eachIssueKey];
            var daysAwayFromDueDate = (eachIssue.daysAwayFromDueDate - daysDifference);
            var componentName = eachIssue.componentsNames[0];
            var avatarId = eachIssue.avatarId;
            var summary = eachIssue.summary;
            var issueKey = eachIssue.key;
            var assigneeName = eachIssue.assigneeName;

            if(daysAwayFromDueDate > numberOfNextDaysToShow) {
                continue;
            }

            if(lateCell != undefined && daysAwayFromDueDate < 0 && eachIssue.done === false) {
                lateCell.appendChild(taskGadgetCreator.createLate(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate == 0 && eachIssue.done === false) {
                todayTasksCell.appendChild(taskGadgetCreator.createToDo(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate == 0 && eachIssue.done === true) {
                todayDoneTasksCell.appendChild(taskGadgetCreator.createDone(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate > 0 && eachIssue.done === true) {
                nextDaysDoneCells[daysAwayFromDueDate].appendChild(taskGadgetCreator.createDone(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate > 0 && eachIssue.done === false) {
                nextDaysCells[daysAwayFromDueDate].appendChild(taskGadgetCreator.createToDo(componentName, avatarId, summary, issueKey, assigneeName));
            }

            if(daysAwayFromDueDate < maximumLate) {
                maximumLate = daysAwayFromDueDate;
            }
        }

        if(this.isViewingCurrentWeek()) {
            datesCreator.createLateDateCell(maximumLate);
        }

        this.fillDates(this.timeLineStartDate);
        this.markDeadlineDateCells();
    };

    this.fillDates = function(timeLineStartDate) {
        var timeLineStartDate = new Date(timeLineStartDate);
        var showingWeekBeforeCurrent = dateUtil.isBeforeToday(timeLineStartDate);

        var daysStartIndex = 0;
        if(this.isViewingCurrentWeek()) {
            datesCreator.createTodayDateCell(1, timeLineStartDate.toDateString());
            daysStartIndex = 1;
        }

        var numberOfNextDaysToShow = 7 - daysStartIndex;
        for(index = daysStartIndex; index < numberOfNextDaysToShow + daysStartIndex; index++) {
            datesCreator.createDateCell(index + daysStartIndex, dateUtil.setNextDayAndGetDateString(timeLineStartDate), showingWeekBeforeCurrent);
        }
    };

    this.markDeadlineDateCells = function() {
        this.deadlineDateIndex = datesCreator.setDeadlineDate(this.deadlineDate);

        var createdTasksCount = Object.keys(tasksCreator.createdTasksCells).length;

        for(index = 0; index < createdTasksCount; index++) {
            if(index === this.deadlineDateIndex) {
                tasksCreator.setAsDeadline(tasksCreator.createdTasksCells[index]);
                tasksCreator.setAsDeadline(tasksCreator.createdDoneTasksCells[index]);
            }
        }
    };

    this.setDeadlineDate = function(dueDate) {
        this.deadlineDate = dueDate;
    }
};