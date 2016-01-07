function TimeLineNavigationButtons(allTimeLineWeeks, weekToShowIndex) {
    var that = this;
    that.currentWeekIndex = 0;
    that.previousWeekButtonId = 'previous-week';
    that.nextWeekButtonId = 'next-week';
    that.currentWeekButtonId = 'current-week';

    that.canMoveToNextWeek = function() {
        return allTimeLineWeeks[weekToShowIndex + 1] != undefined;
    };

    that.canMoveToPreviousWeek = function() {
        return allTimeLineWeeks[weekToShowIndex - 1] != undefined;
    };

    that.isShowingCurrentWeek = function() {
        return weekToShowIndex == that.currentWeekIndex;
    };

    that.getVisibility = function(condition) {
        if(condition === true) {
            return 'visible';
        } else {
            return 'hidden';
        }
    };

    that.getStyleByElementId = function(id) {
        return document.getElementById(id).style;
    }

    that.create = function() {
        var nextWeekButtonStyle = that.getStyleByElementId(that.nextWeekButtonId);
        nextWeekButtonStyle.visibility = that.getVisibility(that.canMoveToNextWeek());

        var previousWeekButtonStyle = that.getStyleByElementId(that.previousWeekButtonId);
        previousWeekButtonStyle.visibility = that.getVisibility(that.canMoveToPreviousWeek());

        var currentWeekButtonStyle = that.getStyleByElementId(that.currentWeekButtonId);
        currentWeekButtonStyle.visibility = that.getVisibility(!that.isShowingCurrentWeek());
    };
};