function TimeLineButtonsListener() {

    this.onPreviousWeekChangeWeekView = function() {
        AJS.$("#previous-week").click(
            function(e) {
                e.preventDefault();
                timeLine.showPreviousWeek();
            }
        );
    };

    this.onNextWeekChangeWeekView = function() {
        AJS.$("#next-week").click(
            function(e) {
                e.preventDefault();
                timeLine.showNextWeek();
            }
        );
    };

    this.onCurrentWeekChangeWeekView = function() {
        AJS.$("#current-week").click(
            function(e) {
                e.preventDefault();
                timeLine.showCurrentWeek();
            }
        );
    };
};