var DUE_DATE_ID = 'event-duedate';
var EVENT_TYPE_ID = 'event-type';

function isDueDateEmpty() {
    "use strict";
    var dueDate = document.getElementById(DUE_DATE_ID).value;
    return dueDate.valueOf() === "".valueOf();
}

function getEventTypeOptions() {
    "use strict";
    return document.getElementById('event-type');
}

function getSelectedEventType() {
    "use strict";
    var eventType = getEventTypeOptions();
    return eventType[eventType.selectedIndex];
}

function sendPostRequest() {
    "use strict";
    var dueDateValue, selectedEventType;
    dueDateValue = document.getElementById(DUE_DATE_ID).value;
    selectedEventType = getSelectedEventType();

    window.location.replace(document.URL +
        "&" +
        DUE_DATE_ID + "=" + dueDateValue +
        "&" +
        EVENT_TYPE_ID + "=" + selectedEventType.id);
}

function configureDatePicker(date) {
    "use strict";
    jQuery('#event-duedate').datetimepicker({
        timepicker: true,
        format: 'd-m-Y H:i',
        value: date,
        todayButton: true
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

    getEventTypeOptions().onchange = function () {
        var date, selectedEventTypeEstimateDaysToComplete;
        selectedEventTypeEstimateDaysToComplete = getSelectedEventType().attributes.days;
        date = new Date();

        if (selectedEventTypeEstimateDaysToComplete !== undefined) {
            date.setDate(date.getDate() + parseInt(selectedEventTypeEstimateDaysToComplete.value, 10));
        }

        configureDatePicker(date);
    };
}