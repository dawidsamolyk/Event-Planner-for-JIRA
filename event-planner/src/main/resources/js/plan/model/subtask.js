function SubTask() {
    "use strict";
    var that = this;

    that.id = 'subtask';

    that.getName = function () {
        return AJS.$("#subtask-name");
    };

    that.getDescription = function () {
        return AJS.$("#subtask-description");
    };

    that.getJson = function () {
        var name, description;
        name = that.getName().attr("value");
        description = that.getDescription().attr("value");

        return '{ "name": "' + name +
            '", "description": "' + description +
            '" }';
    };
};