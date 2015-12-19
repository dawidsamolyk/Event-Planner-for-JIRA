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
    this.onSaveDoPostResource = function() {
        var rest = this.rest;
        var resource = this.resource;

        AJS.$(this.getSaveButtonId()).click(
            function(e) {
                e.preventDefault();
                rest.post(resource);
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
    planListener.onSaveDoGetAndSaveInto(plansList);
    planListener.onCancelCloseDialog();