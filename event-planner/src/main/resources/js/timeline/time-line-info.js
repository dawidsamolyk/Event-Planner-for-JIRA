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

    that.stopCheckingIsAnyTaskWasChanged = function () {
        that.checkIsAnyTaskWasChanged = null;
    };

    that.startCheckingIsAnyTaskWasChanged = function (timeLine) {
        var timeoutTimeInSeconds = 60, millisecondsMultiplier = 1000, flag;
        that.lastRequestTime = new Date().getTime();

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
                            var flagTitleBeginnig = 'Task ';
                            if (data.length > 1) {
                                flagTitleBeginnig = 'Tasks ';
                            }

                            flag = require('aui/flag')({
                                type: 'info',
                                title: flagTitleBeginnig + data + ' was changed.',
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