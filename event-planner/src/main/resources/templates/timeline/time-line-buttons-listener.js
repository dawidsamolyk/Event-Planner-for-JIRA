function TimeLineButtonsListener() {
    this.dateUtil = new DateUtil();

    this.onPreviousWeekChangeWeekView = function(currentTimeLineStartDate) {
        var dateUtil = this.dateUtil;

        AJS.$("#previous-week").click(
            function(e) {
                e.preventDefault();
                dateUtil.substractDaysFromDate(currentTimeLineStartDate, 7);
                timeLine.refresh();
            }
        );
    };

    this.onNextWeekChangeWeekView = function(currentTimeLineStartDate) {
        var dateUtil = this.dateUtil;

        AJS.$("#next-week").click(
            function(e) {
                e.preventDefault();
                dateUtil.addDaysToDate(currentTimeLineStartDate, 7);
                timeLine.refresh();
            }
        );
    };

    this.onCurrentWeekChangeWeekView = function(currentTimeLineStartDate) {
        var dateUtil = this.dateUtil;

        AJS.$("#current-week").click(
            function(e) {
                e.preventDefault();
                currentTimeLineStartDate = new Date();
                timeLine.refresh();
            }
        );
    };
};