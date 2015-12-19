function Component() {
    this.id = 'component';
    this.getName = function() { return AJS.$("#component-name") };
    this.getDescription = function() { return AJS.$("#component-description") };
    this.getTasks = function() { return AJS.$("#component-tasks") };

    this.getJson = function() {
        return '{ "name": "' + this.getName().attr("value") +
               '", "description": "' + this.getDescription().attr("value") +
               '", "tasks": ' + JSON.stringify(this.getTasks().val()) +
               ' }';
    };

    this.setFromJson = function(allTasks) {
        this.getTasks().empty();

        for(eachKey in allTasks) {
            var eachTask = allTasks[eachKey];
            this.getTasks().append("<option>" + eachTask.name + "</option>");
        }
    };
};