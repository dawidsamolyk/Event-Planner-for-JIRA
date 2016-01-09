function Component() {
    var that = this;
    that.id = 'component';
    that.getName = function() { return AJS.$("#component-name") };
    that.getDescription = function() { return AJS.$("#component-description") };
    that.getTasks = function() { return AJS.$("#component-tasks") };

    that.getJson = function() {
        return '{ "name": "' + that.getName().attr("value") +
               '", "description": "' + that.getDescription().attr("value") +
               '", "tasksNames": ' + JSON.stringify(that.getTasks().val()) +
               ' }';
    };

    that.setFromJson = function(allTasks) {
        that.getTasks().empty();

        for(eachKey in allTasks) {
            var eachTask = allTasks[eachKey];
            that.getTasks().append("<option>" + eachTask.name + "</option>");
        }
    };
};