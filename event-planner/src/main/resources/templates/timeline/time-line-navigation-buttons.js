function TimeLineNavigationButtons(allTimeLineWeeks, weekToShowIndex) {
    currentWeekIndex = 0;
    previousWeekButtonId = 'previous-week';
    nextWeekButtonId = 'next-week';
    currentWeekButtonId = 'current-week';

    canMoveToNextWeek = function() {
        return allTimeLineWeeks[weekToShowIndex + 1] != undefined;
    };

    canMoveToPreviousWeek = function() {
        return allTimeLineWeeks[weekToShowIndex - 1] != undefined;
    };

    isShowingCurrentWeek = function() {
        return weekToShowIndex == currentWeekIndex;
    };

    getVisibility = function(condition) {
        if(condition === true) {
            return 'visible';
        } else {
            return 'hidden';
        }
    };

    getStyleByElementId = function(id) {
        return document.getElementById(id).style;
    }

    this.create = function() {
        getStyleByElementId(nextWeekButtonId).visibility = getVisibility(canMoveToNextWeek());
        getStyleByElementId(previousWeekButtonId).visibility = getVisibility(canMoveToPreviousWeek());
        getStyleByElementId(currentWeekButtonId).visibility = getVisibility(!isShowingCurrentWeek());
    };
};