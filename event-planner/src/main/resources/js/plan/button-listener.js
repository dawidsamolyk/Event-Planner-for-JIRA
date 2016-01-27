function ButtonListener(resource) {
    "use strict";
    var that = this;
    that.resource = resource;
    that.rest = new RESTManager();

    that.getButtonId = function (name) {
        return "#event-".concat(that.resource.id).concat("-dialog-" + name + "-button");
    };

    that.toggleForm = function () {
        jQuery("#" + that.resource.id + "-form").toggle();

        jQuery('html, body').animate({
            scrollTop: jQuery('#form-title').offset().top + 'px'
        }, 'slow');
    };

    that.onClick = function (buttonName, functionToExecute) {
        AJS.$(that.getButtonId(buttonName)).click(
            function (e) {
                e.preventDefault();
                functionToExecute();
            }
        );
    };

    that.onCancelBackToEventPlanTemplatesList = function () {
        that.onClick('cancel', function () {
            window.location.href = AJS.contextPath() + '/secure/EventPlansManager.jspa';
        });
    };

    that.onClickClearForm = function (buttonName) {
        that.onClick(buttonName, resource.clear);
    };

    that.onClickHideForm = function (buttonName) {
        that.onClick(buttonName, that.toggleForm);
    };

    that.onClickToggleResourceForm = function (buttonName, resource) {
        that.onClick(buttonName, function () {
            jQuery("#" + resource.id + "-form").toggle();
        });
    };

    that.onSaveDoPostResource = function () {
        that.onClick('save', function () {
            that.rest.post(that.resource);
        });
    };

    that.onAddNewResourceAppendItToAvailableResourcesList = function () {
        that.onClick('add', function () {
            var list = AJS.$("#selected-event-" + that.resource.id), listElement, gadgetContainer;

            if (that.resource.id === 'category') {
                list.append("<li class='ui-state-default aui-label'>" + resource.getNameValue() + "</li>");
                resource.clear();

            } else if (that.resource.id === 'component') {
                listElement = document.createElement('LI');
                listElement.className = 'gadget color1 event-plan-element-portlet';
                listElement.style.position = 'relative';
                listElement.style.zIndex = '1';

                gadgetContainer = document.createElement('DIV');
                gadgetContainer.className = 'dashboard-item-frame gadget-container';
                listElement.appendChild(gadgetContainer);


                list.append("<li class='ui-state-default aui-label'>" + resource.getNameValue() + "</li>");
                resource.clear();
            }
        });
    };
};