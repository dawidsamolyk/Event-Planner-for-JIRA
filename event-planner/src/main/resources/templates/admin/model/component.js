function Component() {
    "use strict";
    var that = this;

    that.id = 'component';

    that.getName = function () {
        return AJS.$("#component-name");
    };

    that.getDescription = function () {
        return AJS.$("#component-description");
    };

    that.getTasks = function () {
        return AJS.$("#component-tasks");
    };

    that.getJson = function () {
        return '{ "name": "' + that.getName().attr("value") +
            '", "description": "' + that.getDescription().attr("value") +
            '", "tasksNames": ' + JSON.stringify(that.getTasks().val()) +
            ' }';
    };

    that.setFromJson = function (allTasks) {
        var eachKey, task;
        that.getTasks().empty();

        for (eachKey in allTasks) {
            task = allTasks[eachKey];

            if (that.isFullFilled(task) === false) {
                return false;
            }
            that.getTasks().append("<option>" + task.name + "</option>");
        }

        return true;
    };

    that.isFullFilled = function (task) {
        return task !== undefined && task.name !== undefined;
    };
};