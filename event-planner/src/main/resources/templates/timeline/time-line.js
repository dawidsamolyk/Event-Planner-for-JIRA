function TimeLine() {
    this.id = 'time-line';
    this.tasksToDoId = 'tasks-todo';
    this.datesId = 'dates';
    this.doneTasksId = 'done-tasks';
    this.deadlineDateIndex;
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

    this.fill = function(dataSource) {
        var lateCell = this.tasksCreator.createLateTaskCell(0);
        this.tasksCreator.createLateDoneTaskCell();
        var todayCell = this.tasksCreator.createTodayTaskCell(1);
        var todayDoneCell = this.tasksCreator.createTodayDoneTaskCell(1);

        if(this.datesCreator.deadlineDateCellIndex === 1) {
            todayCell.setAsDeadline();
            todayDoneCell.setAsDeadline();
        }

        var nextDaysCells = [];
        var nextDaysDoneCells = [];
        var numberOfNextDays = 6;
        for(index = 1; index < numberOfNextDays + 1; index++) {
            nextDaysCells[index] = this.tasksCreator.createTaskCell(index + 1);
            nextDaysDoneCells[index] = this.tasksCreator.createDoneTaskCell(index + 1);

            if(this.datesCreator.deadlineDateCellIndex === index) {
                nextDaysCells[index].setAsDeadline();
                nextDaysDoneCells[index].setAsDeadline();
            }
        }

        var maximumLate = 0;

        for(eachIssueKey in dataSource) {
            var eachIssue = dataSource[eachIssueKey];
            var daysAwayFromDueDate = eachIssue.daysAwayFromDueDate;
            var componentName = eachIssue.componentsNames[0];
            var avatarId = eachIssue.avatarId;
            var summary = eachIssue.summary;
            var issueKey = eachIssue.key;
            var assigneeName = eachIssue.assigneeName;

            if(daysAwayFromDueDate > numberOfNextDays) {
                continue;
            }

            if(daysAwayFromDueDate < 0 && eachIssue.done === false) {
                lateCell.appendChild(this.taskGadgetCreator.createLate(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate == 0 && eachIssue.done === false) {
                todayCell.appendChild(this.taskGadgetCreator.createToDo(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate == 0 && eachIssue.done === true) {
                todayDoneCell.appendChild(this.taskGadgetCreator.createDone(componentName, avatarId, summary, issueKey, assigneeName));
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

        this.datesCreator.createLateDateCell(maximumLate);
        this.fillDates();
        this.setDueDateCells();
    };

    this.fillDates = function() {
        var currentDate = new Date();
        this.datesCreator.createTodayDateCell(1, currentDate.toDateString());

        var numberOfNextDays = 6;
        for(index = 2; index < numberOfNextDays + 2; index++) {
            this.datesCreator.createDateCell(index, this.datesCreator.dateUtil.setNextDayAndGetDateString(currentDate));
        }
    };

    this.setDueDateCells = function() {
        var createdTasksCount = Object.keys(this.tasksCreator.createdTasksCells).length;

        for(index = 0; index < createdTasksCount; index++) {
            if(index === this.deadlineDateIndex) {
                this.tasksCreator.setAsDeadline(this.tasksCreator.createdTasksCells[index]);
                this.tasksCreator.setAsDeadline(this.tasksCreator.createdDoneTasksCells[index]);
            }
        }
    };

    this.setDueDate = function(dueDate) {
        this.deadlineDateIndex = this.datesCreator.setDeadlineDate(dueDate);
    };
};