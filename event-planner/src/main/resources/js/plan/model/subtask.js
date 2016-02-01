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

    that.getNameValue = function () {
        return that.getName().attr("value");
    };

    that.getDescriptionValue = function () {
        return that.getDescription().attr("value");
    };

    that.clear = function () {
        that.getName().val('');
        that.getDescription().val('');
    };

    that.getJson = function () {
        return '{ "name": "' + that.getNameValue() +
            '", "description": "' + that.getDescriptionValue() +
            '" }';
    };
}