function TimeLineInfoProvider(timeLine) {
    var that = this;
    that.tasks = timeLine.tasks;
    that.deadlineDate = timeLine.deadlineDate;
    that.weeksCalculator = new WeeksCalculator();
    that.dateUtil = new DateUtil();

    that.getTimeLineWeeks = function() {
        return that.weeksCalculator.getWeeks(that.getMostLateTaskDueDate(), that.deadlineDate);
    };

    that.getMostLateTaskDueDate = function() {
        return that.dateUtil.getDateWithSubstractedDays(new Date(), that.getMaximumIssueLate());
    };

    that.getPercentOfDoneTasks = function() {
        var result = 0;
        for(eachIssue in that.tasks) {
            var issue = that.tasks[eachIssue];

            if(issue.done === true) {
                result++;
            }
        }
        return (100 * result) / that.tasks.length;
    };

    that.getMaximumIssueLate = function() {
        var result = 0;
        for(eachIssue in that.tasks) {
            var task = that.tasks[eachIssue];
            var daysAwayFromDueDate = task.daysAwayFromDueDate;

            if(daysAwayFromDueDate < result && task.done === false) {
                result = daysAwayFromDueDate;
            }
        }
        return Math.abs(result);
    };

    that.showFlagsIfRequired = function() {
        var today = new Date();
        var daysToDeadline = that.dateUtil.getDaysDifference(that.deadlineDate, today);
        var percentDoneTasks = that.getPercentOfDoneTasks();
        var maximumIssueLate = that.getMaximumIssueLate();

        if(that.dateUtil.isBeforeToday(that.deadlineDate) && percentDoneTasks < 100) {
            var todayDateString = today.toDateString();
            var deadlineDateString = that.deadlineDate.toDateString();
            var message = '<li>Deadline date: ' + deadlineDateString + '.</li>';

            if(percentDoneTasks < 100) message += '<li>Currently there is ' + percentDoneTasks + '% of done tasks.</li>';
            if(that.maximumIssueLate > 0) message += '<li style="color: #d04437;">The most delayed task should be done ' + that.getMaximumIssueLate() + ' days ago!</li>';

            require('aui/flag')({
                type: 'error',
                title: 'Deadline was exceeded by ' + Math.abs(daysToDeadline) + ' days!',
                body: message
            });
        }
        if(percentDoneTasks === 100) {
            require('aui/flag')({
                type: 'success',
                title: 'Congratulations!',
                body: 'All tasks are done! <br />Have a chocolate!'
            });
        }
    };
};