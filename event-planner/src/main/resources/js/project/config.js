var DUE_DATE_ID = 'event-duedate';
var EVENT_TYPE_ID = 'event-type';

function isDueDateEmpty() {
    "use strict";
    var dueDate = document.getElementById(DUE_DATE_ID).value;
    return dueDate.valueOf() === "".valueOf();
}

function sendPostRequest() {
    "use strict";
    var dueDateValue, eventType, eventTypeValue;
    dueDateValue = document.getElementById(DUE_DATE_ID).value;
    eventType = document.getElementById('event-type');
    eventTypeValue = eventType[eventType.selectedIndex].id;

    window.location.replace(document.URL +
        "&" +
        DUE_DATE_ID + "=" + dueDateValue +
        "&" +
        EVENT_TYPE_ID + "=" + eventTypeValue);
}

function configureDatePicker() {
    "use strict";
    jQuery('#event-duedate').datetimepicker({
        timepicker: true,
        format: 'd-m-Y H:i'
    });
}

function configureDialog() {
    "use strict";
    configureDatePicker();

    AJS.$("#event-config-dialog-save-button").click(function (e) {
        e.preventDefault();

        if (!isDueDateEmpty()) {
            sendPostRequest();
            AJS.dialog2("#event-config-dialog").remove();
        } else {
            jQuery('#event-duedate').html('<div class="error" data-field="event-duedate-description">You must specify a date of the event.</div>');
        }
    });
}