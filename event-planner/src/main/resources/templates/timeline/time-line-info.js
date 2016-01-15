function TimeLineInfoProvider(timeLine) {
    "use strict";
    var that = this;
    that.tasks = timeLine.tasks;
    that.deadlineDate = timeLine.deadlineDate;
    that.weeksCalculator = new WeeksCalculator();
    that.dateUtil = new DateUtil();

    that.getTimeLineWeeks = function () {
        return that.weeksCalculator.getWeeks(that.getMostLateTaskDueDate(), that.deadlineDate);
    };

    that.getMostLateTaskDueDate = function () {
        return that.dateUtil.getDateWithSubstractedDays(new Date(), that.getMaximumIssueLate());
    };

    that.getPercentOfDoneTasks = function () {
        var result, eachIssue, issue;
        result = 0;
        for (eachIssue in that.tasks) {
            issue = that.tasks[eachIssue];

            if (issue.done === true) {
                result += 1;
            }
        }
        return 100 * result / that.tasks.length;
    };

    that.getMaximumIssueLate = function () {
        var result, eachIssue, task, daysAwayFromDueDate;
        result = 0;
        for (eachIssue in that.tasks) {
            task = that.tasks[eachIssue];
            daysAwayFromDueDate = task.daysAwayFromDueDate;

            if (daysAwayFromDueDate < result && task.done === false) {
                result = daysAwayFromDueDate;
            }
        }
        return Math.abs(result);
    };

    that.showFlagsIfRequired = function () {
        var today, daysToDeadline, percentDoneTasks, maximumIssueLate, message;
        today = new Date();
        daysToDeadline = that.dateUtil.getDaysDifference(that.deadlineDate, today);
        percentDoneTasks = that.getPercentOfDoneTasks();
        maximumIssueLate = that.getMaximumIssueLate();

        if (that.dateUtil.isBeforeToday(that.deadlineDate) && percentDoneTasks < 100) {
            message = '<li>Deadline date: ' + today.toDateString() + '.</li>';

            if (percentDoneTasks < 100) {
                message += '<li>Currently there is ' + percentDoneTasks + '% of done tasks.</li>';
            }
            if (that.maximumIssueLate > 0) {
                message += '<li style="color: #d04437;">The most delayed task should be done ' + that.getMaximumIssueLate() + ' days ago!</li>';
            }

            require('aui/flag')({
                type: 'error',
                title: 'Deadline was exceeded by ' + Math.abs(daysToDeadline) + ' days!',
                body: message
            });
        }
        if (percentDoneTasks === 100) {
            require('aui/flag')({
                type: 'success',
                title: 'Congratulations!',
                body: 'All tasks are done! <br />Have a chocolate!'
            });
        }
    };
};