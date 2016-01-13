var DUE_DATE_ID = 'event-duedate';
var EVENT_TYPE_ID = 'event-type';

function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
    results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
};

function isDueDateEmpty() {
    var dueDate = document.getElementById(DUE_DATE_ID).value;
    return dueDate.valueOf() == "".valueOf();
};

function sendPostRequest() {
    var dueDateValue = document.getElementById(DUE_DATE_ID).value;
    var eventTypeValue = document.getElementById(EVENT_TYPE_ID).value;

    window.location.replace(document.URL +
                                        "&" +
                                        DUE_DATE_ID + "=" + dueDateValue +
                                        "&" +
                                        EVENT_TYPE_ID + "=" + eventTypeValue);
};

function configureDatePicker() {
   jQuery('#event-duedate').datetimepicker({
       timepicker : true,
       format : 'd-m-Y H:i'
   });
};

function configureDialog() {
    configureDatePicker();

    AJS.$("#event-config-dialog-save-button").click(function(e) {
        e.preventDefault();

        if(!isDueDateEmpty()) {
            sendPostRequest();
            AJS.dialog2("#event-config-dialog").remove();
        } else {
            $('#event-duedate-description').html('<div class="error" data-field="event-duedate">You must specify a date of the event.</div>');
        }
    });
};