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

    that.getNeededMonthsToComplete = function () {
        return AJS.$("#task-needed-months");
    };

    that.getNeededDaysToComplete = function () {
        return AJS.$("#task-needed-days");
    };

    that.getSubTasks = function () {
        return AJS.$("#task-subtasks");
    };

    that.getJson = function () {
        var name, description, neededMonths, neededDays, subTasksNamesArray;
        name = that.getName().attr("value");
        description = that.getDescription().attr("value");
        neededMonths = that.getNeededMonthsToComplete().attr("value");
        neededDays = that.getNeededDaysToComplete().attr("value");
        subTasksNamesArray = JSON.stringify(that.getSubTasks().val());

        return '{ "name": "' + name +
            '", "description": "' + description +
            '", "neededMonths": ' + neededMonths +
            ', "neededDays": ' + neededDays +
            ', "subTasksNames": ' + subTasksNamesArray +
            ' }';
    };

    that.setFromJson = function (allSubTasks) {
        var eachKey, subTask, subTaskName;
        that.getSubTasks().empty();

        for (eachKey in allSubTasks) {
            subTask = allSubTasks[eachKey];
            subTaskName = eachSubTask.name;

            if (subTaskName === undefined) {
                return false;
            }

            that.getSubTasks().append("<option>" + subTaskName + "</option>");
        }
        return true;
    };
};