function TimeLine() {
    this.issues = {};
    this.deadlineDate = new Date();
    this.maximumIssueLate = 0;

    this.tasksToDoId = 'tasks-todo';
    this.datesId = 'dates';
    this.doneTasksId = 'done-tasks';

    this.allTimeLineWeeks = {};
    this.showingWeekIndex = 0;

    this.datesCreator = new TimeLineDatesCreator();
    this.tasksCreator = new TimeLineTasksCreator();

    this.dateUtil = new DateUtil();
    this.weeksCalculator = new WeeksCalculator();

    this.setIssues = function(sourceIssues) {
        this.issues = sourceIssues;
        this.maximumIssueLate = this.getMaximumIssueLate();
    };

    this.setProjectDeadline = function(sourceDeadlineDate) {
        this.deadlineDate = sourceDeadlineDate;
        this.allTimeLineWeeks = this.getTimeLineWeeks();
        this.showCurrentWeek();
    };

    this.getTimeLineWeeks = function() {
        return this.weeksCalculator.getWeeks(this.getMostLateTaskDueDate(), this.deadlineDate);
    };

    this.getMostLateTaskDueDate = function() {
        return this.dateUtil.getDateWithSubstractedDays(new Date(), this.maximumIssueLate);
    };

    this.getMaximumIssueLate = function() {
        var result = 0;
        for(eachIssue in this.issues) {
            var issue = this.issues[eachIssue];
            var daysAwayFromDueDate = issue.daysAwayFromDueDate;

            if(daysAwayFromDueDate < result && issue.done === false) {
                result = daysAwayFromDueDate;
            }
        }
        return Math.abs(result);
    };

    this.showNextWeek = function() {
        this.show(++this.showingWeekIndex);
    };

    this.showPreviousWeek = function() {
        this.show(--this.showingWeekIndex);
    };

    this.showCurrentWeek = function() {
        this.show(0);
    };

    this.show = function(weekToShowIndex) {
        this.clearTimeLine();

        if(this.allTimeLineWeeks.length === 0) {
            // TODO wyświetl komunikat o błędzie
            return;
        }
        this.showingWeekIndex = weekToShowIndex;
        var weekToShow = this.allTimeLineWeeks[weekToShowIndex];

        this.createDatesCells(weekToShow);
        this.createNavigationButtons();

        this.createLateTasksCells();
        this.createTasksCells(weekToShow);
    };

    this.createDatesCells = function(weekToShow) {
        if(this.showingCurrentWeek() && this.maximumIssueLate != 0) {
            this.datesCreator.createLateDateCell(this.maximumIssueLate);
        }
        this.datesCreator.createCells(weekToShow, this.deadlineDate);
    };

    this.showingCurrentWeek = function() {
        return this.showingWeekIndex === 0;
    };

    this.createNavigationButtons = function() {
        var timeLineNavigationButtons = new TimeLineNavigationButtons(this.allTimeLineWeeks, this.showingWeekIndex);
        timeLineNavigationButtons.create();
    };

    this.createLateTasksCells = function() {
        if(this.showingCurrentWeek() && this.maximumIssueLate != 0) {
            var lateCell = this.tasksCreator.createLateTaskCell();
            this.tasksCreator.fillLateCellByIssues(lateCell, this.issues);
        }
    };

    this.createTasksCells = function(weekToShow) {
        var cells = this.tasksCreator.createTasksCells(weekToShow, this.deadlineDate);
        this.tasksCreator.fillCellsByIssues(cells, this.issues);
    };

    this.clearTimeLine = function() {
        this.clearTable(this.tasksToDoId);
        this.clearTable(this.datesId);
        this.clearTable(this.doneTasksId);
    };

    this.clearTable = function(id) {
        var table = document.getElementById(id);
        while(table.cells.length > 0) {
            table.deleteCell(0);
        }
    };
};