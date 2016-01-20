function TimeLine() {
    "use strict";
    var that = this;
    that.tasks = {};
    that.deadlineDate = new Date();

    that.tasksToDoId = 'tasks-todo';
    that.datesId = 'dates';
    that.doneTasksId = 'done-tasks';

    that.allTimeLineWeeks = {};
    that.currentDisplayedWeekIndex = 0;

    that.datesCreator = new TimeLineDatesCreator();
    that.tasksCreator = new TimeLineTasksCreator();
    that.infoProvider = new TimeLineInfoProvider();

    that.setIssues = function (sourceTasks) {
        that.tasks = sourceTasks;
        that.showCurrentWeek();
    };

    that.setProjectDeadline = function (sourceDeadlineDate) {
        that.deadlineDate = sourceDeadlineDate;
        that.showCurrentWeek();
    };

    that.showNextWeek = function () {
        that.show(that.currentDisplayedWeekIndex + 1);
    };

    that.showPreviousWeek = function () {
        that.show(that.currentDisplayedWeekIndex - 1);
    };

    that.showCurrentWeek = function () {
        that.show(0);
    };

    that.show = function (weekToShowIndex) {
        that.clearTimeLine();

        that.allTimeLineWeeks = that.infoProvider.getTimeLineWeeks(that.deadlineDate, that.tasks);
        that.currentDisplayedWeekIndex = weekToShowIndex;
        var weekToShow = that.allTimeLineWeeks[weekToShowIndex];

        that.createDatesCells(weekToShow);
        that.createNavigationButtons();

        that.createLateTasksCells();
        that.createTasksCells(weekToShow);

        that.infoProvider.showFlagsIfRequired(that.deadlineDate, that.tasks);
    };

    that.clearTimeLine = function () {
        that.clearTable(that.tasksToDoId);
        that.clearTable(that.datesId);
        that.clearTable(that.doneTasksId);
    };

    that.clearTable = function (id) {
        var table = document.getElementById(id);
        while (table.cells.length > 0) {
            table.deleteCell(0);
        }
    };

    that.createDatesCells = function (weekToShow) {
        if (that.shouldDisplayLateTasksColumn()) {
            that.datesCreator.createLateDateCell(that.infoProvider.getMaximumIssueLate(that.tasks));
        }
        that.datesCreator.createCells(weekToShow, that.deadlineDate);
    };

    that.shouldDisplayLateTasksColumn = function () {
        return that.isDisplayingCurrentWeek() && that.infoProvider.getMaximumIssueLate(that.tasks) !== 0;
    };

    that.isDisplayingCurrentWeek = function () {
        return that.currentDisplayedWeekIndex === 0;
    };

    that.createNavigationButtons = function () {
        var timeLineNavigationButtons = new TimeLineNavigationButtons(that.allTimeLineWeeks, that.currentDisplayedWeekIndex);
        timeLineNavigationButtons.create();
    };

    that.createLateTasksCells = function () {
        if (that.shouldDisplayLateTasksColumn()) {
            var lateCell = that.tasksCreator.createLateTaskCell();
            that.tasksCreator.fillLateCellByIssues(lateCell, that.tasks);

            return lateCell;
        }
    };

    that.createTasksCells = function (weekToShow) {
        var cells = that.tasksCreator.createTasksCells(weekToShow, that.deadlineDate);
        that.tasksCreator.fillCellsByIssues(cells, that.tasks);

        return cells;
    };
};