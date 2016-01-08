function TimeLine() {
    var that = this;
    that.issues = {};
    that.deadlineDate = new Date();

    that.tasksToDoId = 'tasks-todo';
    that.datesId = 'dates';
    that.doneTasksId = 'done-tasks';

    that.allTimeLineWeeks = {};
    that.currentDisplayedWeekIndex = 0;

    that.datesCreator = new TimeLineDatesCreator();
    that.tasksCreator = new TimeLineTasksCreator();
    that.infoProvider = new TimeLineInfoProvider(that);

    that.setIssues = function(sourceIssues) {
        that.issues = sourceIssues;

        that.showCurrentWeek();
    };

    that.setProjectDeadline = function(sourceDeadlineDate) {
        that.deadlineDate = sourceDeadlineDate;
        that.allTimeLineWeeks = that.infoProvider.getTimeLineWeeks();

        that.showCurrentWeek();
        that.infoProvider.showFlagsIfRequired();
    };

    that.showNextWeek = function() {
        that.show(++that.currentDisplayedWeekIndex);
    };

    that.showPreviousWeek = function() {
        that.show(--that.currentDisplayedWeekIndex);
    };

    that.showCurrentWeek = function() {
        that.show(0);
    };

    that.show = function(weekToShowIndex) {
        that.clearTimeLine();

        that.currentDisplayedWeekIndex = weekToShowIndex;
        var weekToShow = that.allTimeLineWeeks[weekToShowIndex];

        that.createDatesCells(weekToShow);
        that.createNavigationButtons();

        that.createLateTasksCells();
        that.createTasksCells(weekToShow);
    };

    that.clearTimeLine = function() {
        that.clearTable(that.tasksToDoId);
        that.clearTable(that.datesId);
        that.clearTable(that.doneTasksId);
    };

    that.clearTable = function(id) {
        var table = document.getElementById(id);
        while(table.cells.length > 0) {
            table.deleteCell(0);
        }
    };

    that.createDatesCells = function(weekToShow) {
        if(that.infoProvider.shouldDisplayLateTasksColumn()) {
            that.datesCreator.createLateDateCell(that.infoProvider.getMaximumIssueLate());
        }
        that.datesCreator.createCells(weekToShow, that.deadlineDate);
    };

    that.createNavigationButtons = function() {
        var timeLineNavigationButtons = new TimeLineNavigationButtons(that.allTimeLineWeeks, that.currentDisplayedWeekIndex);
        timeLineNavigationButtons.create();
    };

    that.createLateTasksCells = function() {
        if(that.infoProvider.shouldDisplayLateTasksColumn()) {
            var lateCell = that.tasksCreator.createLateTaskCell();
            that.tasksCreator.fillLateCellByIssues(lateCell, that.issues);
        }
    };

    that.createTasksCells = function(weekToShow) {
        var cells = that.tasksCreator.createTasksCells(weekToShow, that.deadlineDate);
        that.tasksCreator.fillCellsByIssues(cells, that.issues);
    };
};