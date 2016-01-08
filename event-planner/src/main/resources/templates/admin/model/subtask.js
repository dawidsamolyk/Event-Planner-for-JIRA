function SubTask() {
    var that = this;
    that.id = 'subtask';
    that.getName = function() { return AJS.$("#subtask-name") };
    that.getDescription = function() { return AJS.$("#subtask-description") };
    that.getTimeToComplete = function() { return AJS.$("#subtask-time") };

    that.getJson = function() {
        return '{ "name": "' + that.getName().attr("value") +
               '", "description": "' + that.getDescription().attr("value") +
               '", "time": ' + that.getTimeToComplete().attr("value") +
               ' }';
    };
};