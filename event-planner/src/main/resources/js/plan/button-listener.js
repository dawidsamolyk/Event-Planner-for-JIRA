function ButtonListener(resource) {
    "use strict";
    var that = this;
    that.resource = resource;
    that.rest = new RESTManager();

    that.getButtonId = function (name) {
        return "#event-".concat(that.resource.id).concat("-dialog-" + name + "-button");
    };

    that.hideForm = function () {
        jQuery("#" + that.resource.id + "-form").hide();
        jQuery("#" + that.resource.id + "-form-buttons").hide();
    };

    that.toggleForm = function () {
        jQuery("#" + that.resource.id + "-form").toggle();
        jQuery("#" + that.resource.id + "-form-buttons").toggle();

        jQuery('html, body').animate({
            scrollTop: jQuery('#page-title').offset().top + 'px'
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
            jQuery("#" + resource.id + "-form-buttons").toggle();
        });
    };

    that.onClickHideElement = function (buttonName, elementId) {
        that.onClick(buttonName, function () {
            jQuery(elementId).hide();
        });
    };

    that.onClickShowElement = function (buttonName, elementId) {
        that.onClick(buttonName, function () {
            jQuery(elementId).show();
        });
    };

    that.onSaveDoPostResource = function () {
        that.onClick('save', function () {
            that.rest.post(that.resource);
        });
    };

    that.onClickAddAppendNewResourceToList = function (list) {
        that.onClick('add', function () {
            if (that.resource.id === 'category' || that.resource.id === 'subtask') {
                list.append("<li class='ui-state-default aui-label'>" + resource.getNameValue() + "</li>");

            } else if (that.resource.id === 'component') {
                list.append(that.getComponentAsGadgetListElement());
                that.resource.getTasks().children('li').appendTo('#' + that.getResourceSubElementsListId());

            } else if (that.resource.id === 'task') {
                list.append(that.getTaskAsListElement());
                that.resource.getSubTasks().children('li').appendTo('#' + that.getResourceSubElementsListId());
            }
            resource.clear();
        });
    };

    that.getResourceSubElementsListId = function () {
        return that.resource.getNameValue() + '-list';
    };

    that.getTaskAsListElement = function () {
        return "<li class='ui-state-default aui-label'>" + resource.getNameValue() + "</li>";
    };

    that.getComponentAsGadgetListElement = function () {
        var listElement, gadgetContainer, header, headerText, details, tasks, loopIndex, eachTask;

        listElement = document.createElement('LI');
        listElement.className = 'gadget color1 event-plan-element-portlet';
        listElement.style.position = 'relative';
        listElement.style.zIndex = '1';

        gadgetContainer = document.createElement('DIV');
        gadgetContainer.className = 'dashboard-item-frame gadget-container';
        listElement.appendChild(gadgetContainer);

        header = document.createElement('DIV');
        header.className = 'dashboard-item-header';

        headerText = document.createElement('H3');
        headerText.className = 'dashboard-item-title';
        headerText.appendChild(document.createTextNode(that.resource.getNameValue()));
        header.appendChild(headerText);
        gadgetContainer.appendChild(header);

        details = document.createElement('DIV');
        details.className = 'dashboard-item-content event-plan-element-portlet-content';

        details.appendChild(document.createTextNode(that.resource.getDescriptionValue()));
        details.appendChild(document.createElement('BR'));
        details.appendChild(document.createTextNode('Tasks:'));
        gadgetContainer.appendChild(details);

        tasks = document.createElement('UL');
        tasks.className = 'tasks-list';
        tasks.id = that.getNewComponentTasksListId();

        details.appendChild(tasks);

        return listElement;
    };

    that.getNewComponentTasksListId = function () {
        return that.resource.getNameValue() + '-tasks-list';
    };
}