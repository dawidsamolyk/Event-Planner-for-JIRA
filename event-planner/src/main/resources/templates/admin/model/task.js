function Task() {
    this.id = 'task';
    this.getName = function() { return AJS.$("#task-name") };
    this.getDescription = function() { return AJS.$("#task-description") };
    this.getTimeToComplete = function() { return AJS.$("#task-time") };
    this.getSubTasks = function() { return AJS.$("#task-subtasks") };

    this.getJson = function() {
        return '{ "name": "' + this.getName().attr("value") +
               '", "description": "' + this.getDescription().attr("value") +
               '", "time": "' + this.getTimeToComplete().attr("value") +
               '", "subtasks": ' + JSON.stringify(this.getSubTasks().val()) +
               ' }';
    };

    this.setFromJson = function(allSubTasks) {
        this.getSubTasks().empty();

        for(eachKey in allSubTasks) {
            var eachSubTask = allSubTasks[eachKey];
            this.getSubTasks().append("<option>" + eachSubTask.name + "</option>");
        }
    };
};