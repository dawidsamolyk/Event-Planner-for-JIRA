function TimeLine() {
    this.id = 'time-line';
    this.tasksToDoId = 'tasks-todo';
    this.datesId = 'dates';
    this.doneTasksId = 'done-tasks';
    this.deadlineDate;
    this.deadlineDateIndex;
    this.timeLineStartDate;
    this.issues;
    this.datesCreator = new TimeLineDateCreator(this.datesId);
    this.tasksCreator = new TimeLineTasksCreator(this.tasksToDoId, this.doneTasksId);
    this.taskGadgetCreator = new TaskGadgetCreator();
    this.getElementById = function(id) { return document.getElementById(id); };

    this.clear = function() {
        this.clearTable(this.tasksToDoId);
        this.clearTable(this.datesId);
        this.clearTable(this.doneTasksId);
    };

    this.clearTable = function(id) {
        var table = this.getElementById(id);
        while(table.cells.length > 0) {
            table.deleteCell(0);
        }
    };

    this.isViewingCurrentWeek = function() {
        return this.datesCreator.dateUtil.isTheSameDay(new Date(), this.timeLineStartDate);
    };

    this.isDeadlineToday = function() {
        this.datesCreator.deadlineDateCellIndex === 1;
    };

    this.refresh = function() {
        this.clear();

        var tasksCellsStartIndex = 0;
        if(this.isViewingCurrentWeek()) {
            var lateCell = this.tasksCreator.createLateTaskCell();
            this.tasksCreator.createLateDoneTaskCell();

            var todayTasksCell = this.tasksCreator.createTodayTaskCell();
            var todayDoneTasksCell = this.tasksCreator.createTodayDoneTaskCell();

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
            nextDaysCells[index] = this.tasksCreator.createTaskCell(index + tasksCellsStartIndex);
            nextDaysDoneCells[index] = this.tasksCreator.createDoneTaskCell(index + tasksCellsStartIndex);

            if(this.datesCreator.deadlineDateCellIndex === index) {
                nextDaysCells[index].setAsDeadline();
                nextDaysDoneCells[index].setAsDeadline();
            }
        }

        var timeDifference = this.timeLineStartDate.getTime() - new Date().getTime();
        var daysDifference = Math.ceil(timeDifference / (1000 * 3600 * 24));

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
                lateCell.appendChild(this.taskGadgetCreator.createLate(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate == 0 && eachIssue.done === false) {
                todayTasksCell.appendChild(this.taskGadgetCreator.createToDo(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate == 0 && eachIssue.done === true) {
                todayDoneTasksCell.appendChild(this.taskGadgetCreator.createDone(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate > 0 && eachIssue.done === true) {
                nextDaysDoneCells[daysAwayFromDueDate].appendChild(this.taskGadgetCreator.createDone(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate > 0 && eachIssue.done === false) {
                nextDaysCells[daysAwayFromDueDate].appendChild(this.taskGadgetCreator.createToDo(componentName, avatarId, summary, issueKey, assigneeName));
            }

            if(daysAwayFromDueDate < maximumLate) {
                maximumLate = daysAwayFromDueDate;
            }
        }

        if(this.isViewingCurrentWeek()) {
            this.datesCreator.createLateDateCell(maximumLate);
        }

        this.fillDates(this.timeLineStartDate);
        this.markDeadlineDateCells();
    };

    this.isBeforeToday = function(date) {
        var today = new Date();
        return date < today && date.getDate() != today.getDate();
    };

    this.fillDates = function(timeLineStartDate) {
        var timeLineStartDate = new Date(timeLineStartDate);
        var showingWeekBeforeCurrent = this.isBeforeToday(timeLineStartDate);

        var daysStartIndex = 0;
        if(this.isViewingCurrentWeek()) {
            this.datesCreator.createTodayDateCell(1, timeLineStartDate.toDateString());
            daysStartIndex = 1;
        }

        var numberOfNextDaysToShow = 7 - daysStartIndex;
        for(index = daysStartIndex; index < numberOfNextDaysToShow + daysStartIndex; index++) {
            this.datesCreator.createDateCell(index + daysStartIndex, this.datesCreator.dateUtil.setNextDayAndGetDateString(timeLineStartDate), showingWeekBeforeCurrent);
        }
    };

    this.markDeadlineDateCells = function() {
        this.deadlineDateIndex = this.datesCreator.setDeadlineDate(this.deadlineDate);

        var createdTasksCount = Object.keys(this.tasksCreator.createdTasksCells).length;

        for(index = 0; index < createdTasksCount; index++) {
            if(index === this.deadlineDateIndex) {
                this.tasksCreator.setAsDeadline(this.tasksCreator.createdTasksCells[index]);
                this.tasksCreator.setAsDeadline(this.tasksCreator.createdDoneTasksCells[index]);
            }
        }
    };

    this.setDeadlineDate = function(dueDate) {
        this.deadlineDate = dueDate;
    }
};