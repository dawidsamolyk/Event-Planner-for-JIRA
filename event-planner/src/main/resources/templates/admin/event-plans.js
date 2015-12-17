function Plan() {
    this.id = 'plan';
    this.getName = function() { return AJS.$("#plan-name") };
    this.getDescription = function() { return AJS.$("#plan-description") };
    this.getTimeToComplete = function() { return AJS.$("#plan-time") };
    this.getDomains = function() { return AJS.$("#plan-domains") };
    this.getComponents = function() { return AJS.$("#plan-components") };

    this.getJson = function() {
        return '{ "name": "' + this.getName().attr("value") +
               '", "description": "' + this.getDescription().attr("value") +
               '", "time": "' + this.getTimeToComplete().attr("value") +
               '", "domains": ' + JSON.stringify(this.getDomains().val()) +
               ', "components": ' + JSON.stringify(this.getComponents().val()) +
               ' }';
    };

    this.setFromJson = function(allResources) {
        for(eachKey in allResources) {
            var eachResource = allResources[eachKey];

            // Component
            if(eachResource.hasOwnProperty('tasks')) {
                this.getComponents().append("<option>" + eachResource.name + "</option>");
            }
            // Domain
            else {
                this.getDomains().append("<option>" + eachResource.name + "</option>");
            }
        }
    };
};

function Domain() {
    this.id = 'domain';
    this.getName = function() { return AJS.$("#domain-name") };
    this.getDescription = function() { return AJS.$("#domain-description") };

    this.getJson = function() {
        return '{ "name": "' + this.getName().attr("value") +
               '", "description": "' + this.getDescription().attr("value") +
               '" }';
    };
};

function Component() {
    this.id = 'component';
    this.getName = function() { return AJS.$("#component-name") };
    this.getDescription = function() { return AJS.$("#component-description") };
    this.getTasks = function() { return AJS.$("#component-tasks") };

    this.getJson = function() {
        return '{ "name": "' + this.getName().attr("value") +
               '", "description": "' + this.getDescription().attr("value") +
               '", "tasks": ' + JSON.stringify(this.getTasks().val()) +
               ' }';
    };

    this.setFromJson = function(allTasks) {
        for(eachKey in allTasks) {
            var eachTask = allTasks[eachKey];
            this.getTasks().append("<option>" + eachTask.name + "</option>");
        }
    };
};

function Task() {
    this.id = 'task';
    this.getName = function() { return AJS.$("#task-name") };
    this.getDescription = function() { return AJS.$("#task-description") };
    this.getTimeToComplete = function() { return AJS.$("#task-time") };
    this.getSubTasks = function() { return AJS.$("#task-subtasks") };

    this.getJson = function() {
        return '{ "name": "' + this.getName().attr("value") +
               '", "description": "' + this.getDescription().attr("value") +
               '", "time": "' + this.getTimeToComplete().attr("value") +
               '", "subtasks": ' + JSON.stringify(this.getSubTasks().val()) +
               ' }';
    };

    this.setFromJson = function(allSubTasks) {
        for(eachKey in allSubTasks) {
            var eachSubTask = allSubTasks[eachKey];
            this.getSubTasks().append("<option>" + eachSubTask.name + "</option>");
        }
    };
};

function SubTask() {
    this.id = 'subtask';
    this.getName = function() { return AJS.$("#subtask-name") };
    this.getDescription = function() { return AJS.$("#subtask-description") };
    this.getTimeToComplete = function() { return AJS.$("#subtask-time") };

    this.getJson = function() {
        return '{ "name": "' + this.getName().attr("value") +
               '", "description": "' + this.getDescription().attr("value") +
               '", "time": "' + this.getTimeToComplete().attr("value") +
               ' }';
    };
};

function RESTManager() {
    this.baseUrl = AJS.contextPath() + "/rest/event-plans/1.0/";

    this.put = function(resource) {
        var resourceId = resource.id;
        var resourceDataAsJson = resource.getJson();

        jQuery.ajax({
            url: this.baseUrl + resourceId,
            type: "PUT",
            contentType: "application/json",
            data: resourceDataAsJson,
            processData: false
        });
    };

    this.get = function(resource) {
        var result;

        jQuery.ajax({
            url: this.baseUrl + resource.id,
            type: "GET",
            dataType: "json"
        })
        .done(function(config) {
            result = config;
        });

        return result;
    };
};

function ButtonListener(resource) {
    this.resource = resource;
    this.rest = new RESTManager();

    this.getResourceId = function() {
        return resource.id;
    };
    this.getDialogId = function() {
        return "#event-".concat(this.getResourceId()).concat("-dialog");
    };
    this.getSaveButtonId = function() {
        return "#event-".concat(this.getResourceId()).concat("-dialog-save-button");
    }
    this.onAddShowDialog = function() {
        var showButtonId = "#add-".concat(this.getResourceId()).concat("-button");
        var formId = "event-".concat(this.getResourceId()).concat("-configuration");
        var dialogId = this.getDialogId();

        AJS.$(showButtonId).click(
            function(e) {
                document.getElementById(formId).reset();
                AJS.dialog2(dialogId).show();
            }
        );
        return this;
    };
    this.onSaveDoPostResource = function() {
        var rest = this.rest;
        var resource = this.resource;
        var dialogId = this.getDialogId();

        AJS.$(this.getSaveButtonId()).click(
            function(e) {
                // TODO walidacja lub złapanie exceptiona i wyświetlenie błędu na ekranie
                rest.put(resource);
            }
        );
        return this;
    };
    this.onSaveHideDialog = function() {
            var dialogId = this.getDialogId();

            AJS.$(this.getSaveButtonId()).click(
                function(e) {
                    AJS.dialog2(dialogId).hide();
                }
            );
            return this;
    };
    this.onSaveDoGet = function(resources) {
            var rest = this.rest;
            var resource = this.resource;

            AJS.$(this.getSaveButtonId()).click(
                function(e) {
                    for(eachKey in resources) {
                        var eachResource = resources[eachKey];
                        var eachJson = rest.get(eachResource);
                        resource.setFromJson(eachJson);
                    }
                }
            );
            return this;
    };
    this.onCancelCloseDialog = function() {
        var cancelButtonId = "#event-".concat(this.getResourceId()).concat("-dialog-cancel-button");
        var dialogId = this.getDialogId();

        AJS.$(cancelButtonId).click(
            function(e) {
                AJS.dialog2(dialogId).hide();
            }
        );
        return this;
    };
    this.onShowDoGet = function(resources) {
        var rest = this.rest;
        var resource = this.resource;

        AJS.dialog2(this.getDialogId()).on("show",
            function() {
                for(eachKey in resources) {
                    var eachResource = resources[eachKey];
                    var eachJson = rest.get(eachResource);
                    resource.setFromJson(eachJson);
                }
            }
        );
        return this;
    };
};

var plan = new Plan();
var domain = new Domain();
var component = new Component();
var task = new Task();
var subTask = new SubTask();

var subTaskListener = new ButtonListener(subTask);
    subTaskListener.onAddShowDialog();
    subTaskListener.onSaveDoPostResource();
    subTaskListener.onSaveHideDialog();
    subTaskListener.onCancelCloseDialog();

var taskListener = new ButtonListener(task);
    taskListener.onShowDoGet([subTask]);
    taskListener.onAddShowDialog();
    taskListener.onSaveDoPostResource();
    taskListener.onSaveDoGet([subTask]);
    taskListener.onSaveHideDialog();
    taskListener.onCancelCloseDialog();

var componentListener = new ButtonListener(component);
    componentListener.onShowDoGet([task]);
    componentListener.onAddShowDialog();
    componentListener.onSaveDoPostResource();
    componentListener.onSaveDoGet([task]);
    componentListener.onSaveHideDialog();
    componentListener.onCancelCloseDialog();

var domainListener = new ButtonListener(domain);
    domainListener.onAddShowDialog();
    domainListener.onSaveDoPostResource();
    domainListener.onSaveHideDialog();
    domainListener.onCancelCloseDialog();

var planListener = new ButtonListener(plan)
    planListener.onShowDoGet([domain, component]);
    planListener.onAddShowDialog();
    planListener.onSaveDoPostResource();
    planListener.onSaveDoGet([domain, component]);
    planListener.onSaveHideDialog();
    planListener.onCancelCloseDialog();