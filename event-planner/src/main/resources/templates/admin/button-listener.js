function ButtonListener(resource) {
    "use strict";
    var that = this;
    that.resource = resource;
    that.rest = new RESTManager();

    that.getResourceId = function () {
        return resource.id;
    };

    that.getResourceDialogId = function (resourceId) {
        return "#event-".concat(resourceId).concat("-dialog");
    };

    that.getDialogId = function () {
        return "#event-".concat(that.getResourceId()).concat("-dialog");
    };

    that.getSaveButtonId = function () {
        return that.getResourceDialogId(that.getResourceId());
    };

    that.getAddButtonId = function () {
        return that.getButtonId('add');
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

    that.onNextShowDialogForResourceId = function (resourceId) {
        AJS.$(that.getNextButtonId()).click(
            function (e) {
                e.preventDefault();
                AJS.dialog2(that.getDialogId()).hide();
                AJS.dialog2(that.getResourceDialogId(resourceId)).show();
            }
        );
    };

    that.onBackShowDialogForResourceId = function (resourceId) {
        AJS.$(that.getBackButtonId()).click(
            function (e) {
                e.preventDefault();
                AJS.dialog2(that.getDialogId()).hide();
                AJS.dialog2(that.getResourceDialogId(resourceId)).show();
            }
        );
    }

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

    that.onShowDoGetResource = function (resourcesArray) {
        AJS.dialog2(that.getDialogId()).on("show",
            function () {
                var eachKey, resource;
                
                for (eachKey in resourcesArray) {
                    resource = resourcesArray[eachKey];
                    that.rest.get(resource.id, resource);
                }
            }
        );
    };

    that.onAddNewResourceAppendItToAvailableResourcesList = function () {
        AJS.$(that.getAddButtonId()).click(
            function (e) {
                e.preventDefault();

                var list = AJS.$("#selected-event-" + that.getResourceId());

                if (that.getResourceId() === 'category') {
                    list.append("<li class='ui-state-default aui-label'>" + resource.getNameValue() + "</li>");
                    resource.clear();
                }
            }
        );
    };
};