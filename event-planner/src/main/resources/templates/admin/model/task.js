function Task() {
    var that = this;
    that.id = 'task';
    that.getName = function() { return AJS.$("#task-name") };
    that.getDescription = function() { return AJS.$("#task-description") };
    that.getTimeToComplete = function() { return AJS.$("#task-time") };
    that.getSubTasks = function() { return AJS.$("#task-subtasks") };

    that.getJson = function() {
        return '{ "name": "' + that.getName().attr("value") +
               '", "description": "' + that.getDescription().attr("value") +
               '", "time": ' + that.getTimeToComplete().attr("value") +
               ', "subtasks": ' + JSON.stringify(that.getSubTasks().val()) +
               ' }';
    };

    that.setFromJson = function(allSubTasks) {
        that.getSubTasks().empty();

        for(eachKey in allSubTasks) {
            var eachSubTask = allSubTasks[eachKey];
            that.getSubTasks().append("<option>" + eachSubTask.name + "</option>");
        }
    };
};