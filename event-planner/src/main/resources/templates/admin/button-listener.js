function ButtonListener(resource) {
    "use strict";
    var that = this;
    that.resource = resource;
    that.rest = new RESTManager();

    that.getResourceId = function () {
        return resource.id;
    };

    that.getResourceDialogId = function (resource) {
        return "#event-".concat(resource.id).concat("-dialog");
    };

    that.getDialogId = function () {
        return "#event-".concat(that.getResourceId()).concat("-dialog");
    };

    that.getSaveButtonId = function () {
        return that.getResourceDialogId(that.getResourceId());
    };

    that.getNextButtonId = function () {
        return that.getButtonId('next');
    };

    that.getBackButtonId = function () {
        return that.getButtonId('back');
    };

    that.getCancelButtonId = function () {
        return that.getButtonId('cancel');
    };

    that.getButtonId = function (name) {
        return "#event-".concat(that.getResourceId()).concat("-dialog-" + name + "-button");
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

    that.onNextShowDialogForResource = function (resource) {
        AJS.$(that.getNextButtonId()).click(
            function (e) {
                e.preventDefault();
                AJS.dialog2(that.getDialogId()).hide();
                AJS.dialog2(that.getResourceDialogId(resource)).show();
            }
        );
    };

    that.onBackShowDialogForResource = function (resource) {
        AJS.$(that.getBackButtonId()).click(
            function (e) {
                e.preventDefault();
                AJS.dialog2(that.getDialogId()).hide();
                AJS.dialog2(that.getResourceDialogId(resource)).show();
            }
        );
    }

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

    that.onSaveClearForm = function () {
        AJS.$(that.getSaveButtonId()).click(
            function (e) {
                e.preventDefault();
                that.resource.clear();
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
        AJS.$(that.getCancelButtonId()).click(
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