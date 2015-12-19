function SubTask() {
    this.id = 'subtask';
    this.getName = function() { return AJS.$("#subtask-name") };
    this.getDescription = function() { return AJS.$("#subtask-description") };
    this.getTimeToComplete = function() { return AJS.$("#subtask-time") };

    this.getJson = function() {
        return '{ "name": "' + this.getName().attr("value") +
               '", "description": "' + this.getDescription().attr("value") +
               '", "time": "' + this.getTimeToComplete().attr("value") +
               ' }';
    };
};