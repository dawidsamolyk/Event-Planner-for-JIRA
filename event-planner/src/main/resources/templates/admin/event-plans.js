function PlansList() {
    this.id = 'plans-table-body';
    this.getTable = function() { return document.getElementById(this.id) };

    this.setFromJson = function(allPlans) {
        this.clearTable();

        for(eachKey in allPlans) {
            var eachPlan = allPlans[eachKey];
            this.insert(eachPlan);
        }
    };

    this.clearTable = function() {
        var table = this.getTable();
        while(table.rows.length > 0) {
            table.deleteRow(0);
        }
    };

    this.insert = function(plan) {
        var table = this.getTable();
        var newRow = table.insertRow(table.rows.length);

        var name = document.createElement('SPAN');
        name.className = "field-name";
        name.dataSchemeField = "name";
        name.appendChild(document.createTextNode(plan.name));
        this.insertCell(newRow, 0, name);

        var description = document.createTextNode(plan.description);
        this.insertCell(newRow, 1, description);

        // TODO create <li> for each
        var domains = document.createTextNode(plan.domains);
        console.log(plan.domains);
        this.insertCell(newRow, 2, domains);

        // TODO create <li> for each
        var components = document.createTextNode(plan.components);
        this.insertCell(newRow, 3, components);

        var time = document.createTextNode(plan.time);
        this.insertCell(newRow, 4, time);

        var operations = document.createTextNode('Edit Copy Delete');
        this.insertCell(newRow, 5, operations);
    };

    this.insertCell = function(row, index, element) {
        var newCell = row.insertCell(index);
        newCell.appendChild(element);
    };

    this.createLiForEach(array) {
    }
};

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
    };

    this.onAddShowDialog = function() {
        var addButtonId = "#add-".concat(this.getResourceId()).concat("-button");
        var formId = "event-".concat(this.getResourceId()).concat("-configuration");
        var dialogId = this.getDialogId();

        AJS.$(addButtonId).click(
            function(e) {
                e.preventDefault();
                document.getElementById(formId).reset();
                AJS.dialog2(dialogId).show();
            }
        );
    };
    this.onSaveDoGetResourceAndFill = function(objectToFill) {
        var rest = this.rest;
        var resourceId = this.getResourceId();

        AJS.$(this.getSaveButtonId()).click(
            function(e) {
                e.preventDefault();
                rest.get(resourceId, objectToFill);
            }
        );
    };
    this.onSaveDoPutResource = function() {
        var rest = this.rest;
        var resource = this.resource;

        AJS.$(this.getSaveButtonId()).click(
            function(e) {
                e.preventDefault();
                rest.put(resource);
            }
        );
    };
    this.onSaveHideDialog = function() {
        var dialogId = this.getDialogId();

        AJS.$(this.getSaveButtonId()).click(
            function(e) {
                e.preventDefault();
                AJS.dialog2(dialogId).hide();
            }
        );
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
    };
};

var plansList = new PlansList();
var plan = new Plan();
var domain = new Domain();
var component = new Component();
var task = new Task();
var subTask = new SubTask();

AJS.$(document).ready(
    function() {
        var rest = new RESTManager();
        rest.get(plan.id, plansList);
});

var subTaskListener = new ButtonListener(subTask);
    subTaskListener.onAddShowDialog();
    subTaskListener.onSaveDoPutResource();
    subTaskListener.onSaveHideDialog();
    subTaskListener.onSaveDoGetAndSaveInto(task);
    subTaskListener.onCancelCloseDialog();

var taskListener = new ButtonListener(task);
    taskListener.onShowDoGet([subTask]);
    taskListener.onAddShowDialog();
    taskListener.onSaveDoPutResource();
    taskListener.onSaveDoGetAndSaveInto(component);
    taskListener.onSaveHideDialog();
    taskListener.onCancelCloseDialog();

var componentListener = new ButtonListener(component);
    componentListener.onShowDoGet([task]);
    componentListener.onAddShowDialog();
    componentListener.onSaveDoPutResource();
    componentListener.onSaveDoGetAndSaveInto(plan);
    componentListener.onSaveHideDialog();
    componentListener.onCancelCloseDialog();

var domainListener = new ButtonListener(domain);
    domainListener.onAddShowDialog();
    domainListener.onSaveDoPutResource();
    domainListener.onSaveHideDialog();
    domainListener.onSaveDoGetAndSaveInto(plan);
    domainListener.onCancelCloseDialog();

var planListener = new ButtonListener(plan)
    planListener.onShowDoGet([domain, component]);
    planListener.onAddShowDialog();
    planListener.onSaveDoPutResource();
    planListener.onSaveHideDialog();
    planListener.onSaveDoGetResourceAndFill(plansList);
    planListener.onCancelCloseDialog();