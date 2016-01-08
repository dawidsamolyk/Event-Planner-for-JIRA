function ButtonListener(resource) {
    var that = this;
    that.resource = resource;
    that.rest = new RESTManager();

    that.getResourceId = function() {
        return resource.id;
    };
    that.getDialogId = function() {
        return "#event-".concat(that.getResourceId()).concat("-dialog");
    };
    that.getSaveButtonId = function() {
        return "#event-".concat(that.getResourceId()).concat("-dialog-save-button");
    };

    that.onAddShowDialog = function() {
        var addButtonId = "#add-".concat(that.getResourceId()).concat("-button");
        var formId = "event-".concat(that.getResourceId()).concat("-configuration");
        var dialogId = that.getDialogId();

        AJS.$(addButtonId).click(
            function(e) {
                e.preventDefault();
                document.getElementById(formId).reset();
                AJS.dialog2(dialogId).show();
            }
        );
    };
    that.onSaveDoGetAndSaveInto = function(destinationResource) {
            var rest = that.rest;
            var resource = that.resource;

            AJS.$(that.getSaveButtonId()).click(
                function(e) {
                    e.preventDefault();
                    rest.get(resource.id, destinationResource);
                }
            );
    };
    that.onSaveDoPostResource = function() {
        var rest = that.rest;
        var resource = that.resource;

        AJS.$(that.getSaveButtonId()).click(
            function(e) {
                e.preventDefault();
                rest.post(resource);
            }
        );
    };
    that.onSaveHideDialog = function() {
        var dialogId = that.getDialogId();

        AJS.$(that.getSaveButtonId()).click(
            function(e) {
                e.preventDefault();
                AJS.dialog2(dialogId).hide();
            }
        );
    };
    that.onCancelCloseDialog = function() {
        var cancelButtonId = "#event-".concat(that.getResourceId()).concat("-dialog-cancel-button");
        var dialogId = that.getDialogId();

        AJS.$(cancelButtonId).click(
            function(e) {
                e.preventDefault();
                AJS.dialog2(dialogId).hide();
            }
        );
    };
    that.onShowDoGet = function(resources) {
        var rest = that.rest;
        var resource = that.resource;

        AJS.dialog2(that.getDialogId()).on("show",
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