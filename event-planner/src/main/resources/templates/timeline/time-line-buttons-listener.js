function TimeLineButtonsListener() {
    var that = this;

    that.onPreviousWeekChangeWeekView = function() {
        AJS.$("#previous-week").click(
            function(e) {
                e.preventDefault();
                timeLine.showPreviousWeek();
            }
        );
    };

    that.onNextWeekChangeWeekView = function() {
        AJS.$("#next-week").click(
            function(e) {
                e.preventDefault();
                timeLine.showNextWeek();
            }
        );
    };

    that.onCurrentWeekChangeWeekView = function() {
        AJS.$("#current-week").click(
            function(e) {
                e.preventDefault();
                timeLine.showCurrentWeek();
            }
        );
    };
};