function Task() {
    "use strict";
    var that = this;

    that.id = 'task';

    that.getName = function () {
        return AJS.$("#" + that.id + "-name");
    };

    that.getNameValue = function () {
        return that.getName().attr("value");
    };

    that.getDescription = function () {
        return AJS.$("#" + that.id + "-description");
    };

    that.getNeededMonthsBeforeEvent = function () {
        return AJS.$("#" + that.id + "-needed-months");
    };

    that.getNeededDaysBeforeEvent = function () {
        return AJS.$("#" + that.id + "-needed-days");
    };

    that.getSubTasks = function () {
        return jQuery("#subtasks");
    };

    that.getAvailableTasks = function () {
        return AJS.$("#available-" + that.id);
    };

    that.getSelectedTasks = function () {
        return AJS.$("#selected-" + that.id);
    };

    that.clear = function () {
        that.getName().val('');
        that.getDescription().val('');
        that.getNeededMonthsBeforeEvent().val('');
        that.getNeededDaysBeforeEvent().val('');
        that.getSubTasks().empty();
    };

    that.getJson = function () {
        var name, description, neededMonthsBeforeEvent, neededDaysBeforeEvent, subTasksNamesArray;
        name = that.getName().attr("value");
        description = that.getDescription().attr("value");
        neededMonthsBeforeEvent = that.getNeededMonthsBeforeEvent().attr("value");
        neededDaysBeforeEvent = that.getNeededDaysBeforeEvent().attr("value");
        subTasksNamesArray = JSON.stringify(that.getSubTasks().val());

        return '{ "name": "' + name +
            '", "description": "' + description +
            '", "neededMonthsBeforeEvent": ' + neededMonthsBeforeEvent +
            ', "neededDaysBeforeEvent": ' + neededDaysBeforeEvent +
            ', "subTasksNames": ' + subTasksNamesArray +
            ' }';
    };

    that.setFromJson = function (data) {
        var eachKey, task, availableTasks;
        availableTasks = that.getAvailableTasks();
        availableTasks.empty();

        for (eachKey in data) {
            task = data[eachKey];

            if (that.isFullFilled(task) === false) {
                return false;
            }
            availableTasks.append("<li class='ui-state-default aui-label event-plan-list-element' title='" + task.description + "'>" + task.name + "</li>");
        }

        return true;
    };

    that.isFullFilled = function (task) {
        return task !== undefined && task.name !== undefined;
    };
}