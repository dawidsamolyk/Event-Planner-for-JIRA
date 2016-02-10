function TimeLineInfoProvider() {
    "use strict";
    var that = this;
    that.weeksCalculator = new WeeksCalculator();
    that.dateUtil = new DateUtil();
    that.checkIsAnyTaskWasChanged = null;
    that.lastRequestTime = new Date().getTime();

    that.getTimeLineWeeks = function (deadlineDate, tasks) {
        return that.weeksCalculator.getWeeks(that.getMostLateTaskDueDate(tasks), deadlineDate);
    };

    that.getMostLateTaskDueDate = function (tasks) {
        return that.dateUtil.getDateWithSubstractedDays(new Date(), that.getMaximumIssueLate(tasks));
    };

    that.getPercentOfDoneTasks = function (tasks) {
        var result, eachIssue, task;
        result = 0;

        if (tasks === undefined || tasks === null) {
            return result;
        }

        for (eachIssue in tasks) {
            task = tasks[eachIssue];

            if (task.status === 'done') {
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

            if (task.status !== 'done' && daysAwayFromDueDate < result) {
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

    that.stopCheckingIsAnyTaskWasChanged = function () {
        that.checkIsAnyTaskWasChanged = null;
    };

    that.startCheckingIsAnyTaskWasChanged = function (timeLine) {
        var timeoutTimeInSeconds = 30, millisecondsMultiplier = 1000, flag;

        that.checkIsAnyTaskWasChanged = function () {
            window.setTimeout(function () {
                var each, tasksKeys = [];

                for (each in timeLine.tasks) {
                    tasksKeys.push(timeLine.tasks[each].key);
                }

                jQuery.ajax({
                    url: AJS.contextPath() + "/rest/event-plans/1.0/changes",
                    type: "POST",
                    contentType: "application/json",
                    data: '{ "projectKey": "' + projectKey + '", "lastRequestTime": ' + that.lastRequestTime + ', "currentTasksKeys": ' + JSON.stringify(tasksKeys) + ' }',
                    dataType: "json",
                    processData: false,
                    success: function (data, textStatus, jqXHR) {
                        if (jqXHR.statusText === "OK" && flag === undefined) {
                            flag = require('aui/flag')({
                                type: 'info',
                                title: 'Tasks ' + data + ' was changed.',
                                body: '<a href="#" id="refresh-timeLine" style="cursor: hand;">Refresh TimeLine</a>'
                            });

                            AJS.$('#refresh-timeLine').click(function (e) {
                                e.preventDefault();
                                timeLine.refresh();
                                flag.close();
                                flag = undefined;
                            });
                        }
                    },
                    complete: that.checkIsAnyTaskWasChanged
                });

                that.lastRequestTime = new Date().getTime();
            }, timeoutTimeInSeconds * millisecondsMultiplier);
        };
        that.checkIsAnyTaskWasChanged();
    };
}