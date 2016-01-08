function Task() {
    var that = this;
    that.id = 'task';
    that.getName = function() { return AJS.$("#task-name") };
    that.getDescription = function() { return AJS.$("#task-description") };
    that.getNeededMonthsToComplete = function() { return AJS.$("#task-needed-months") };
    that.getNeededDaysToComplete = function() { return AJS.$("#task-needed-days") };
    that.getSubTasks = function() { return AJS.$("#task-subtasks") };

    that.getJson = function() {
        return '{ "name": "' + that.getName().attr("value") +
               '", "description": "' + that.getDescription().attr("value") +
               '", "neededMonths": ' + that.getNeededMonthsToComplete().attr("value") +
               ', "neededDays": ' + that.getNeededDaysToComplete().attr("value") +
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