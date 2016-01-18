function Task() {
    "use strict";
    var that = this;

    that.id = 'task';

    that.getName = function () {
        return AJS.$("#task-name");
    };

    that.getDescription = function () {
        return AJS.$("#task-description");
    };

    that.getNeededMonthsBeforeEvent = function () {
        return AJS.$("#task-needed-months");
    };

    that.getNeededDaysBeforeEvent = function () {
        return AJS.$("#task-needed-days");
    };

    that.getSubTasks = function () {
        return AJS.$("#task-subtasks");
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

    that.setFromJson = function (allSubTasks) {
        var eachKey, subTask;
        that.getSubTasks().empty();

        for (eachKey in allSubTasks) {
            subTask = allSubTasks[eachKey];

            if (subTask.name === undefined) {
                return false;
            }
            that.getSubTasks().append("<option>" + subTask.name + "</option>");
        }
        return true;
    };
};