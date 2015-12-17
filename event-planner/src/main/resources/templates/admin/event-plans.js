function Plan() {
    this.name = AJS.$("#plan-name");
    this.description = AJS.$("#plan-description");
    this.timeToComplete = AJS.$("#plan-time");
    this.domains = AJS.$("#plan-domains")
    this.components = AJS.$("#plan-components");

    this.getJson = function() {
        return '"name": "' + this.name.attr("value") +
               '", "description": "' + this.description.attr("value") +
               '", "time": "' + this.timeToComplete.attr("value") +
               '", "domains": ' + JSON.stringify(this.domains.val()) +
               ', "components": ' + JSON.stringify(this.components.val()) +
               ' }';
    }

    this.setDomainsFromJson = function(jsonObject) {

    }

    this.setComponentsFromJson = function(jsonObject) {

    };
};

function Domain() {
    this.name = AJS.$("#domain-name");
    this.description = AJS.$("#domain-description");

    this.getJson = function() {
        return '"name": "' + this.name.attr("value") +
               '", "description": "' + this.description.attr("value") +
               '" }';
    };
};

function Component() {
    this.name = AJS.$("#component-name");
    this.description = AJS.$("#component-description");
    this.tasks = AJS.$("#component-tasks");

    this.getJson = function() {
        return '"name": "' + this.name.attr("value") +
               '", "description": "' + this.description.attr("value") +
               '", "tasks": ' + JSON.stringify(this.tasks.val()) +
               ' }';
    };
};

function Task() {
    this.name = AJS.$("#task-name");
    this.description = AJS.$("#task-description");
    this.timeToComplete = AJS.$("#task-time");
    this.subtasks = AJS.$("#task-subtasks");

    this.getJson = function() {
        return '"name": "' + this.name.attr("value") +
               '", "description": "' + this.description.attr("value") +
               '", "time": "' + this.timeToComplete.attr("value") +
               '", "subtasks": ' + JSON.stringify(this.subtasks.val()) +
               ' }';
    };
};

function SubTask() {
    this.name = AJS.$("#subtask-name");
    this.description = AJS.$("#subtask-description");
    this.timeToComplete = AJS.$("#subtask-time");

    this.getJson = function() {
        return '"name": "' + this.name.attr("value") +
               '", "description": "' + this.description.attr("value") +
               '", "time": "' + this.timeToComplete.attr("value") +
               ' }';
    };
};

function Selector() {
    this.getJsonFor = function(resourceName) {
        var result;
        switch(resourceName) {
            case 'plan':
                result = new Plan();
                break;
            case 'domain':
                result = new Domain();
                break;
            case 'component':
                result = new Component();
                break;
            case 'task':
                result = new Task();
                break;
            case 'subtask':
                result = new SubTask();
                break;
            default:
                return null;
                break;
        }
        return result.getJson();
    };
};

function RESTManager() {
    this.baseUrl = AJS.contextPath() + "/rest/event-plans/1.0/";
    this.selector = new Selector();

    this.put = function(jsonAsText, resourceName) {
        jQuery.ajax({
            url: baseUrl + resourceName,
            type: "PUT",
            contentType: "application/json",
            data: jsonAsText,
            processData: false
        });
    };

    this.put = function(resourceName) {
        this.put(selector.getJsonFor(resourceName), resourceName);
    };

    this.get = function(resourceName) {
        var result;

        jQuery.ajax({
            url: baseUrl + resourceName,
            type: "GET",
            dataType: "json"
        })
        .done(function(config) {
            result = config;
        });

        return result;
    };
};

var rest = new RESTManager();

function getElementById(id) {
    return document.getElementById(id).value;
};

function addButtonListenerFor(resourceName) {
    AJS.$("#add-" + resourceName + "-button").click(
        function(e) {
            e.preventDefault();

            var formId = resourceName.concat("-configuration");
            document.getElementById(formId).reset();

            var dialogId = "#event-".concat(resourceName).concat("-dialog");
            AJS.dialog2(dialogId).show();
        }
    );
};

addButtonListenerFor('plan');
addButtonListenerFor('domain');
addButtonListenerFor('component');
addButtonListenerFor('task');
addButtonListenerFor('subtask');



        AJS.$("#event-plan-dialog-save-button").click(function(e) {
            e.preventDefault();

            var planName = getElementById("plan-name");
            var planTime = getElementById("plan-time");
            var domains = getElementById("plan-domains");
            var components = getElementById("plan-components");
            var valid = true;

            if(planName.valueOf() == "".valueOf()) {
                AJS.$('#plan-name-description').html('<div class="error" data-field="plan-name">You must specify a name of event Plan.</div>');
                valid = false;
            }
            if(planTime.valueOf() == "".valueOf()) {
                AJS.$('#plan-time-description').html('<div class="error" data-field="plan-time">You must specify estimated tine for this kind of event Plan.</div>');
                valid = false;
            }
            if(domains.valueOf() == "".valueOf()) {
                AJS.$('#plan-domains-description').html('<div class="error" data-field="plan-domains">You must select at least one Domain of event Plan.</div>');
                valid = false;
            }
            if(components.valueOf() == "".valueOf()) {
                AJS.$('#plan-components-description').html('<div class="error" data-field="plan-components">You must select at least one Component of event Plan.</div>');
                valid = false;
            }

            if(valid) {
                // TODO Sprawdź czy obiekt Plan o tej nazwie już istnieje
                rest.put('plan');

                AJS.dialog2("#event-plan-dialog").hide();
            }
        });

        AJS.$("#event-plan-dialog-cancel-button").click(function(e) {
            e.preventDefault();
            AJS.dialog2("#event-plan-dialog").hide();
            AJS.dialog2("#event-component-dialog").hide();
            AJS.dialog2("#event-domain-dialog").hide();
        });

        AJS.$("#event-domain-dialog-save-button").click(function(e) {
            e.preventDefault();

            var domainName = document.getElementById("domains-name").value;

            if(domainName.valueOf() == "".valueOf()) {
                AJS.$('#domains-name-description').html('<div class="error" data-field="domains-name">You must specify a name of the event Domain.</div>');
            } else {
                // TODO Sprawdź czy obiekt Domain o tej nazwie już istnieje

                rest.put('domain');

                AJS.dialog2("#event-domain-dialog").hide();
                AJS.dialog2("#event-plan-dialog").show();
            }
        });

        AJS.$("#event-domain-dialog-cancel-button").click(function(e) {
            e.preventDefault();

            AJS.dialog2("#event-domain-dialog").hide();
            AJS.dialog2("#event-plan-dialog").show();
        });

        AJS.$("#event-component-dialog-save-button").click(function(e) {
            e.preventDefault();

            var name = document.getElementById("component-name").value;
            var tasks = document.getElementById("component-tasks").value;

            if(name.valueOf() == "".valueOf()) {
                AJS.$('#component-name-description').html('<div class="error" data-field="component-name">You must specify a name of the event Component.</div>');
            }
            if(tasks.valueOf() == "".valueOf()) {
                AJS.$('#component-tasks-description').html('<div class="error" data-field="component-tasks">You must specify Tasks of which Component consists.</div>');
            }
            else {
                // TODO Sprawdź czy obiekt Component o tej nazwie już istnieje

                rest.put('component');

                AJS.dialog2("#event-component-dialog").hide();
                AJS.dialog2("#event-plan-dialog").show();
            }
        });

        AJS.$("#event-component-dialog-cancel-button").click(function(e) {
            e.preventDefault();

            AJS.dialog2("#event-component-dialog").hide();
            AJS.dialog2("#event-plan-dialog").show();
        });

        AJS.$("#event-task-dialog-save-button").click(function(e) {
            e.preventDefault();

            var name = document.getElementById("task-name").value;
            var time = document.getElementById("task-time").value;

            if(name.valueOf() == "".valueOf()) {
                AJS.$('#task-name-description').html('<div class="error" data-field="task-name">You must specify a name of the Task.</div>');
            }
            if(time.valueOf() == "".valueOf()) {
                AJS.$('#task-time-description').html('<div class="error" data-field="task-time">You must specify estimated time to complete Task.</div>');
            }
            else {
                // TODO Sprawdź czy obiekt Component o tej nazwie już istnieje

                rest.put('task');

                AJS.dialog2("#event-task-dialog").hide();
                AJS.dialog2("#event-plan-dialog").show();
            }
        });

        AJS.$("#event-task-dialog-cancel-button").click(function(e) {
            e.preventDefault();

            AJS.dialog2("#event-task-dialog").hide();
            AJS.dialog2("#event-plan-dialog").show();
        });


        AJS.$("#event-subtask-dialog-save-button").click(function(e) {
            e.preventDefault();

            var name = document.getElementById("subtask-name").value;
            var time = document.getElementById("subtask-time").value;

            if(name.valueOf() == "".valueOf()) {
                AJS.$('#subtask-name-description').html('<div class="error" data-field="subtask-name">You must specify a name of the SubTask.</div>');
            }
            if(time.valueOf() == "".valueOf()) {
                AJS.$('#subtask-time-description').html('<div class="error" data-field="subtask-time">You must specify estimated time to complete SubTask.</div>');
            }
            else {
                // TODO Sprawdź czy obiekt Component o tej nazwie już istnieje

                rest.put('subtask');

                AJS.dialog2("#event-subtask-dialog").hide();
                AJS.dialog2("#event-plan-dialog").show();
            }
        });

        AJS.$("#event-subtask-dialog-cancel-button").click(function(e) {
            e.preventDefault();

            AJS.dialog2("#event-subtask-dialog").hide();
            AJS.dialog2("#event-plan-dialog").show();
        });