function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
    results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
};

var project = getParameterByName('project');
var projectKey = project.substring(9, project.length);

if(projectKey) {
    var pageTitle = projectKey + " - Event Organization Time Line";
    document.title = pageTitle;
    document.getElementById("time-line-name").innerHTML = pageTitle;
} else {
    document.title = "Event Organization Time Line";
}

var timeLine = new TimeLine();

AJS.$(document).ready(
    function() {
        var rest = new RESTManager();
        rest.getIssues(projectKey, timeLine);
        rest.getProjectDeadline(projectKey, timeLine);
});

var buttonsListener = new TimeLineButtonsListener();
    buttonsListener.onPreviousWeekChangeWeekView();
    buttonsListener.onNextWeekChangeWeekView();
    buttonsListener.onCurrentWeekChangeWeekView();