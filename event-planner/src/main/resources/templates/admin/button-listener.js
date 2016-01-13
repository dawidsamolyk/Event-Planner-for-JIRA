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