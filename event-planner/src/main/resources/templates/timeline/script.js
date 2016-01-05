function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
    results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
};

var timeLine = new TimeLine();
timeLine.clear();

var projectKey = getParameterByName('project-key');

var pageTitle = projectKey + " - Event Organization Time Line";
document.title = pageTitle;
document.getElementById("time-line-name").innerHTML = pageTitle;

AJS.$(document).ready(
    function() {
        var rest = new RESTManager();
        rest.getIssues(projectKey, timeLine);
        rest.getProjectDueDate(projectKey, timeLine);
});