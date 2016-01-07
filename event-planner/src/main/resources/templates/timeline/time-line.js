function TimeLine() {
    var that = this;
    that.issues = {};
    that.deadlineDate = new Date();
    that.maximumIssueLate = 0;
    that.percentDoneTasks = 0;

    that.tasksToDoId = 'tasks-todo';
    that.datesId = 'dates';
    that.doneTasksId = 'done-tasks';

    that.allTimeLineWeeks = {};
    that.showingWeekIndex = 0;

    that.datesCreator = new TimeLineDatesCreator();
    that.tasksCreator = new TimeLineTasksCreator();

    that.dateUtil = new DateUtil();
    that.weeksCalculator = new WeeksCalculator();

    that.setIssues = function(sourceIssues) {
        that.issues = sourceIssues;
        that.maximumIssueLate = that.getMaximumIssueLate();
        that.percentDoneTasks = that.getPercentOfDoneTasks();console.log(that.percentDoneTasks);
        that.showCurrentWeek();
    };

    that.setProjectDeadline = function(sourceDeadlineDate) {
        that.deadlineDate = sourceDeadlineDate;
        that.allTimeLineWeeks = that.getTimeLineWeeks();
        that.showCurrentWeek();

        var today = new Date();
        var daysToDeadline = that.dateUtil.getDaysDifference(that.deadlineDate, today);

        if(that.dateUtil.isBeforeToday(that.deadlineDate) && that.percentDoneTasks < 100) {
            var todayDateString = today.toDateString();
            var deadlineDateString = that.deadlineDate.toDateString();
            var message = '<li>Deadline date: ' + deadlineDateString + '.</li>';

            if(that.percentDoneTasks < 100) message += '<li>Currently there is ' + that.percentDoneTasks + '% of done tasks.</li>';
            if(that.maximumIssueLate > 0) message += '<li style="color: #d04437;">The most delayed task should be done ' + that.maximumIssueLate + ' days ago!</li>';

            AJS.flag({
                type: 'error',
                title: 'Deadline was exceeded by ' + Math.abs(daysToDeadline) + ' days!',
                body: message
            });
        }
        if(that.percentDoneTasks === 100 && that.maximumIssueLate === 0) {
            AJS.flag({
                type: 'success',
                title: 'Congratulations!',
                body: 'All tasks are done! <br />Have a chocolate!'
            });
        }
        else if(daysToDeadline >= 0) {
            var message = '';

            if(daysToDeadline > 0) message += '<li>There are ' + daysToDeadline + ' days to deadline.</li>';
            else if(daysToDeadline === 0) message += '<li style="color: #14892c;">Deadline is today!</li>';

            if(that.percentDoneTasks < 100) message += '<li>Currently there is ' + that.percentDoneTasks + '% of done tasks.</li>';
            if(that.maximumIssueLate > 0) message += '<li style="color: #d04437;">The most delayed task should be done ' + that.maximumIssueLate + ' days ago!</li>';

            AJS.flag({
                type: 'info',
                body: message
            });
        }
    };

    that.getTimeLineWeeks = function() {
        return that.weeksCalculator.getWeeks(that.getMostLateTaskDueDate(), that.deadlineDate);
    };

    that.getMostLateTaskDueDate = function() {
        return that.dateUtil.getDateWithSubstractedDays(new Date(), that.maximumIssueLate);
    };

    that.getPercentOfDoneTasks = function() {
        var result = 0;
        for(eachIssue in that.issues) {
            var issue = that.issues[eachIssue];

            if(issue.done === true) {
                result++;
            }
        }
        return (100 * result) / that.issues.length;
    };

    that.getMaximumIssueLate = function() {
        var result = 0;
        for(eachIssue in that.issues) {
            var issue = that.issues[eachIssue];
            var daysAwayFromDueDate = issue.daysAwayFromDueDate;

            if(daysAwayFromDueDate < result && issue.done === false) {
                result = daysAwayFromDueDate;
            }
        }
        return Math.abs(result);
    };

    that.showNextWeek = function() {
        that.show(++that.showingWeekIndex);
    };

    that.showPreviousWeek = function() {
        that.show(--that.showingWeekIndex);
    };

    that.showCurrentWeek = function() {
        that.show(0);
    };

    that.show = function(weekToShowIndex) {
        that.clearTimeLine();

        if(that.allTimeLineWeeks.length === 0) {
            AJS.flag({
                type: 'error',
                title: 'Deadline was exceeded by ' + Math.abs(daysToDeadline) + ' days!',
                body: message
            });
            return;
        }
        that.showingWeekIndex = weekToShowIndex;
        var weekToShow = that.allTimeLineWeeks[weekToShowIndex];

        that.createDatesCells(weekToShow);
        that.createNavigationButtons();

        that.createLateTasksCells();
        that.createTasksCells(weekToShow);
    };

    that.createDatesCells = function(weekToShow) {
        if(that.showingCurrentWeek() && that.maximumIssueLate != 0) {
            that.datesCreator.createLateDateCell(that.maximumIssueLate);
        }
        that.datesCreator.createCells(weekToShow, that.deadlineDate);
    };

    that.showingCurrentWeek = function() {
        return that.showingWeekIndex === 0;
    };

    that.createNavigationButtons = function() {
        var timeLineNavigationButtons = new TimeLineNavigationButtons(that.allTimeLineWeeks, that.showingWeekIndex);
        timeLineNavigationButtons.create();
    };

    that.createLateTasksCells = function() {
        if(that.showingCurrentWeek() && that.maximumIssueLate != 0) {
            var lateCell = that.tasksCreator.createLateTaskCell();
            that.tasksCreator.fillLateCellByIssues(lateCell, that.issues);
        }
    };

    that.createTasksCells = function(weekToShow) {
        var cells = that.tasksCreator.createTasksCells(weekToShow, that.deadlineDate);
        that.tasksCreator.fillCellsByIssues(cells, that.issues);
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
};