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
    that.draggableListener = new DraggableListener();

    that.setIssues = function (sourceTasks) {
        that.tasks = sourceTasks;
        if (that.deadlineDate !== undefined) {
            that.showCurrentDisplayedWeek();
        }
    };

    that.setProjectDeadline = function (sourceDeadlineDate) {
        that.deadlineDate = sourceDeadlineDate;
        if (that.tasks !== undefined) {
            that.showCurrentDisplayedWeek();
        }
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
        var weekToShow, lateCells, cells;
        that.clearTimeLine();

        that.allTimeLineWeeks = that.infoProvider.getTimeLineWeeks(that.deadlineDate, that.tasks);
        that.currentDisplayedWeekIndex = weekToShowIndex;
        weekToShow = that.allTimeLineWeeks[weekToShowIndex];

        that.createDatesCells(weekToShow);
        that.createNavigationButtons();

        lateCells = that.createLateTasksCells();
        cells = that.createTasksCells(weekToShow);

        that.draggableListener.connect(jQuery.extend(lateCells, cells), that);

        that.infoProvider.showFlagsIfRequired(that.deadlineDate, that.tasks);
    };

    that.showCurrentDisplayedWeek = function () {
        that.show(that.currentDisplayedWeekIndex);
    };

    that.refresh = function () {
        loadDataForTimeLine();
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
            var cells = that.tasksCreator.createLateTaskCells();
            that.tasksCreator.fillLateCellByIssues(cells, that.tasks);

            return cells;
        }
    };

    that.createTasksCells = function (weekToShow) {
        var cells = that.tasksCreator.createTasksCells(weekToShow, that.deadlineDate);
        that.tasksCreator.fillCellsByIssues(cells, that.tasks);

        return cells;
    };
};