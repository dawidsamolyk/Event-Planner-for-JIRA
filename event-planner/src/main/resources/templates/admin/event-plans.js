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
        var isComponent = (allResources.length > 0 && allResources[0].hasOwnProperty('tasks'));

        if(isComponent === true) {
            this.getComponents().empty();
        } else {
            this.getDomains().empty();
        }
        
        for(eachKey in allResources) {
            var eachResource = allResources[eachKey];
            var eachOption = "<option>" + eachResource.name + "</option>";

            if(isComponent) {
                this.getComponents().append(eachOption);
            } else {
                this.getDomains().append(eachOption);
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
        this.getTasks().empty();

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
        this.getSubTasks().empty();

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
        jQuery.ajax({
            url: this.baseUrl + resource.id,
            type: "PUT",
            contentType: "application/json",
            data: resource.getJson(),
            processData: false,
            error: function (request, status, error) {
                // Wyświetlenie komunikatu o błędzie
            }
        });
    };

    this.get = function(sourceId, destinationResource) {
        jQuery.ajax({
            url: this.baseUrl + sourceId,
            type: "GET",
            dataType: "json",
            success: function (data, status, request) {
                destinationResource.setFromJson(data);
            },
            error: function (request, status, error) {
                // Wyświetlenie komunikatu o błędzie
            }
        });
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
                e.preventDefault();

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
                e.preventDefault();

                rest.put(resource);
            }
        );
        return this;
    };
    this.onSaveHideDialog = function() {
            var dialogId = this.getDialogId();

            AJS.$(this.getSaveButtonId()).click(
                function(e) {
                    e.preventDefault();

                    AJS.dialog2(dialogId).hide();
                }
            );
            return this;
    };
    this.onSaveDoGetAndSaveInto = function(destinationResource) {
            var rest = this.rest;
            var resource = this.resource;

            AJS.$(this.getSaveButtonId()).click(
                function(e) {
                    e.preventDefault();

                    rest.get(resource.id, destinationResource);
                }
            );
            return this;
    };
    this.onCancelCloseDialog = function() {
        var cancelButtonId = "#event-".concat(this.getResourceId()).concat("-dialog-cancel-button");
        var dialogId = this.getDialogId();

        AJS.$(cancelButtonId).click(
            function(e) {
                e.preventDefault();

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
                    rest.get(eachResource.id, resource);
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
    subTaskListener.onSaveDoGetAndSaveInto(task);
    subTaskListener.onCancelCloseDialog();

var taskListener = new ButtonListener(task);
    taskListener.onShowDoGet([subTask]);
    taskListener.onAddShowDialog();
    taskListener.onSaveDoPostResource();
    taskListener.onSaveDoGetAndSaveInto(component);
    taskListener.onSaveHideDialog();
    taskListener.onCancelCloseDialog();

var componentListener = new ButtonListener(component);
    componentListener.onShowDoGet([task]);
    componentListener.onAddShowDialog();
    componentListener.onSaveDoPostResource();
    componentListener.onSaveDoGetAndSaveInto(plan);
    componentListener.onSaveHideDialog();
    componentListener.onCancelCloseDialog();

var domainListener = new ButtonListener(domain);
    domainListener.onAddShowDialog();
    domainListener.onSaveDoPostResource();
    domainListener.onSaveHideDialog();
    domainListener.onSaveDoGetAndSaveInto(plan);
    domainListener.onCancelCloseDialog();

var planListener = new ButtonListener(plan)
    planListener.onShowDoGet([domain, component]);
    planListener.onAddShowDialog();
    planListener.onSaveDoPostResource();
    planListener.onSaveHideDialog();
    planListener.onCancelCloseDialog();