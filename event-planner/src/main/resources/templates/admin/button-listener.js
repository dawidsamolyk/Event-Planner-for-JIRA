function ButtonListener(resource) {
    "use strict";
    var that = this;
    that.resource = resource;
    that.rest = new RESTManager();

    that.getResourceId = function () {
        return resource.id;
    };

    that.getDialogId = function () {
        return "#event-".concat(that.getResourceId()).concat("-dialog");
    };

    that.getSaveButtonId = function () {
        return "#event-".concat(that.getResourceId()).concat("-dialog-save-button");
    };

    that.onAddShowDialog = function () {
        var addButtonId, formId;
        addButtonId = "#add-".concat(that.getResourceId()).concat("-button");
        formId = "event-".concat(that.getResourceId()).concat("-configuration");

        AJS.$(addButtonId).click(
            function (e) {
                e.preventDefault();
                document.getElementById(formId).reset();
                AJS.dialog2(that.getDialogId()).show();
            }
        );
    };

    that.onSaveDoGetAndSaveInto = function (destinationResource) {
        AJS.$(that.getSaveButtonId()).click(
            function (e) {
                e.preventDefault();
                that.rest.get(that.resource.id, destinationResource);
            }
        );
    };

    that.onSaveDoPostResource = function () {
        AJS.$(that.getSaveButtonId()).click(
            function (e) {
                e.preventDefault();
                that.rest.post(that.resource);
            }
        );
    };

    that.onSaveHideDialog = function () {
        AJS.$(that.getSaveButtonId()).click(
            function (e) {
                e.preventDefault();
                AJS.dialog2(that.getDialogId()).hide();
            }
        );
    };

    that.onCancelCloseDialog = function () {
        var cancelButtonId = "#event-".concat(that.getResourceId()).concat("-dialog-cancel-button");

        AJS.$(cancelButtonId).click(
            function (e) {
                e.preventDefault();
                AJS.dialog2(that.getDialogId()).hide();
            }
        );
    };

    that.onShowDoGet = function (resources) {
        AJS.dialog2(that.getDialogId()).on("show",
            function () {
                var eachKey, eachResource;
                for (eachKey in resources) {
                    eachResource = resources[eachKey];
                    that.rest.get(eachResource.id, that.resource);
                }
            }
        );
    };
};