function Component() {
    "use strict";
    var that = this;

    that.id = 'component';

    that.getName = function () {
        return AJS.$("#" + that.id + "-name");
    };

    that.getNameValue = function () {
        return that.getName().attr("value");
    };

    that.getDescription = function () {
        return AJS.$("#" + that.id + "-description");
    };

    that.getDescriptionValue = function () {
        return that.getDescription().attr("value");
    };

    that.getTasks = function () {
        return jQuery("#" + that.id + "-tasks");
    };

    that.getTasksValue = function () {
        return that.getTasks().val();
    };

    that.getTasksValue = function () {
        return that.getTasks().val();
    };

    that.getAvailableComponents = function () {
        return AJS.$("#available-" + that.id);
    };

    that.getSelectedComponents = function () {
        return AJS.$("#selected-" + that.id);
    };

    that.clear = function () {
        that.getTasks().empty();
        that.getName().val('');
        that.getDescription().val('');
    };

    that.getJson = function () {
        return '{ "name": "' + that.getTasksValue() +
            '", "description": "' + that.getDescriptionValue() +
            '", "tasksNames": ' + JSON.stringify(that.getTasksValue()) +
            ' }';
    };

    that.setFromJson = function (data) {
        var eachKey, component, availableComponents;
        availableComponents = that.getAvailableComponents();
        availableComponents.empty();

        for (eachKey in data) {
            component = data[eachKey];

            if (that.isFullFilled(component) === false) {
                return false;
            }
            availableComponents.append(that.getComponentGadget(component));
        }

        return true;
    };

    that.getNeededTimeAsText = function (resource) {
        var result = '';

        if (resource.neededMonthsBeforeEvent === 1) {
            result += resource.neededMonthsBeforeEvent + ' month';
        } else if (resource.neededMonthsBeforeEvent > 1) {
            result += resource.neededMonthsBeforeEvent + ' months';
        }
        if (resource.neededMonthsBeforeEvent > 0 && resource.neededDaysBeforeEvent > 0) {
            result += resource.neededDaysBeforeEvent + ' and ';
        }
        if (resource.neededDaysBeforeEvent === 1) {
            result += resource.neededDaysBeforeEvent + ' day';
        } else if (resource.neededDaysBeforeEvent > 1) {
            result += resource.neededDaysBeforeEvent + ' days';
        }
        return result;
    };

    that.getComponentGadget = function (componentData) {
        var listElement, container, header, headerText, content, tasksList, taskListElement, loopIndex, tasks, task, neededTime, maxTime;

        tasks = componentData.task;

        listElement = document.createElement("LI");
        listElement.className = 'gadget color1 event-plan-element-portlet';

        container = document.createElement("DIV");
        container.className = 'dashboard-item-frame gadget-container';
        listElement.appendChild(container);

        header = document.createElement("DIV");
        header.className = 'dashboard-item-header';
        container.appendChild(header);

        headerText = document.createElement("H3");
        headerText.className = 'dashboard-item-title';
        headerText.appendChild(document.createTextNode(componentData.name));
        header.appendChild(headerText);

        content = document.createElement("DIV");
        content.className = 'dashboard-item-content event-plan-element-portlet-content';
        if (componentData.description !== undefined && componentData.description.length > 0) {
            content.appendChild(document.createTextNode(componentData.description));
            content.appendChild(document.createElement('BR'));
        }
        content.appendChild(document.createTextNode('Tasks:'));

        tasksList = document.createElement("UL");
        tasksList.className = 'tasks-list';
        maxTime = {neededMonthsBeforeEvent: 0, neededDaysBeforeEvent: 0};

        for (loopIndex = 0; loopIndex < tasks.length; loopIndex += 1) {
            task = tasks[loopIndex];

            taskListElement = document.createElement("LI");
            taskListElement.className = 'ui-state-default aui-label event-plan-list-element';
            if (task.description !== undefined && task.description.length > 0) {
                taskListElement.title = task.description;
            }
            taskListElement.title = task.name;

            neededTime = ' (' + that.getNeededTimeAsText(task) + ')';

            taskListElement.appendChild(document.createTextNode(task.name + neededTime));

            tasksList.appendChild(taskListElement);

            if (that.getEstimatedDays(task) > that.getEstimatedDays(maxTime)) {
                maxTime.neededMonthsBeforeEvent = task.neededMonthsBeforeEvent;
                maxTime.neededDaysBeforeEvent = task.neededDaysBeforeEvent;
            }
        }

        content.appendChild(tasksList);
        content.appendChild(document.createTextNode('Estimated time to complete all: ' + that.getNeededTimeAsText(maxTime)));
        container.appendChild(content);

        return listElement;
    };

    that.getEstimatedDays = function (resource) {
        return resource.neededMonthsBeforeEvent * 30 + resource.neededDaysBeforeEvent;
    };

    that.isFullFilled = function (component) {
        return component !== undefined && component.name !== undefined && component.task !== undefined && component.task.length > 0;
    };
}