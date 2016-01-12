function SubTask() {
    var that = this;
    that.id = 'subtask';
    that.getName = function() { return AJS.$("#subtask-name") };
    that.getDescription = function() { return AJS.$("#subtask-description") };

    that.getJson = function() {
        var name = that.getName().attr("value");
        var description = that.getDescription().attr("value");

        return '{ "name": "' + name +
               '", "description": "' + description +
               '" }';
    };
};