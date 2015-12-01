var DUE_DATE = 'event-duedate';
var EVENT_TYPE = 'event-type';

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
    results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

function isDueDateEmpty() {
    var dueDate = document.getElementById(DUE_DATE).value;
    return dueDate.valueOf() == "".valueOf();
}

function sendPostRequest() {
    var dueDateValue = document.getElementById(DUE_DATE).value;
    var eventTypeValue = document.getElementById(EVENT_TYPE).value;

    window.location.replace(document.URL +
                                        "&" +
                                        DUE_DATE + "=" + dueDateValue +
                                        "&" +
                                        EVENT_TYPE + "=" + eventTypeValue);
}

AJS.dialog2("#event-config-dialog").show();

AJS.$("#event-config-dialog-save-button").click(function(e) {
    e.preventDefault();

    if(!isDueDateEmpty()) {
        sendPostRequest();
        AJS.dialog2("#event-config-dialog").remove();
    } else {
        $('#event-duedate-description').html('<div class="error" data-field="event-duedate">You must specify a date of the event.</div>');
    }
});