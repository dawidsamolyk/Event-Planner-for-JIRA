function TimeLineInfoProvider() {
    "use strict";
    var that = this;
    that.weeksCalculator = new WeeksCalculator();
    that.dateUtil = new DateUtil();

    that.getTimeLineWeeks = function (deadlineDate, tasks) {
        return that.weeksCalculator.getWeeks(that.getMostLateTaskDueDate(tasks), deadlineDate);
    };

    that.getMostLateTaskDueDate = function (tasks) {
        return that.dateUtil.getDateWithSubstractedDays(new Date(), that.getMaximumIssueLate(tasks));
    };

    that.getPercentOfDoneTasks = function (tasks) {
        var result, eachIssue, issue;
        result = 0;
        for (eachIssue in tasks) {
            issue = tasks[eachIssue];

            if (issue.done === true) {
                result += 1;
            }
        }
        return 100 * result / tasks.length;
    };

    that.getMaximumIssueLate = function (tasks) {
        var result = 0, eachIssue, task, daysAwayFromDueDate;

        for (eachIssue in tasks) {
            task = tasks[eachIssue];
            daysAwayFromDueDate = task.daysAwayFromDueDate;

            if (task.done === false && daysAwayFromDueDate < result) {
                result = daysAwayFromDueDate;
            }
        }
        return Math.abs(result);
    };

    that.showFlagsIfRequired = function (deadlineDate, tasks) {
        var today, daysToDeadline, percentDoneTasks, maximumIssueLate, message;
        today = new Date();
        daysToDeadline = that.dateUtil.getDaysDifference(deadlineDate, today);
        percentDoneTasks = that.getPercentOfDoneTasks(tasks);
        maximumIssueLate = that.getMaximumIssueLate(tasks);

        if (that.dateUtil.isBeforeToday(deadlineDate) && percentDoneTasks < 100) {
            message = '<li>Deadline date: ' + today.toDateString() + '.</li>';

            if (percentDoneTasks < 100) {
                message += '<li>Currently there is ' + percentDoneTasks + '% of done tasks.</li>';
            }
            if (that.maximumIssueLate > 0) {
                message += '<li style="color: #d04437;">The most delayed task should be done ' + maximumIssueLate + ' days ago!</li>';
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