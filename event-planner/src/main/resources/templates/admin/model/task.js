function Task() {
    var that = this;
    that.id = 'task';
    that.getName = function() { return AJS.$("#task-name") };
    that.getDescription = function() { return AJS.$("#task-description") };
    that.getNeededMonthsToComplete = function() { return AJS.$("#task-needed-months") };
    that.getNeededDaysToComplete = function() { return AJS.$("#task-needed-days") };
    that.getSubTasks = function() { return AJS.$("#task-subtasks") };

    that.getJson = function() {
        var name = that.getName().attr("value");
        var description = that.getDescription().attr("value");
        var neededMonths = that.getNeededMonthsToComplete().attr("value");
        var neededDays = that.getNeededDaysToComplete().attr("value");
        var subTasksNamesArray = JSON.stringify(that.getSubTasks().val());

        return '{ "name": "' + name +
               '", "description": "' + description +
               '", "neededMonths": ' + neededMonths +
               ', "neededDays": ' + neededDays +
               ', "subTasksNames": ' + subTasksNamesArray +
               ' }';
    };

    that.setFromJson = function(allSubTasks) {
        that.getSubTasks().empty();

        for(eachKey in allSubTasks) {
            var eachSubTask = allSubTasks[eachKey];
            var subTaskName = eachSubTask.name;

            if(subTaskName === undefined) {
                return false;
            }

            that.getSubTasks().append("<option>" + subTaskName + "</option>");
        }
        return true;
    };
};