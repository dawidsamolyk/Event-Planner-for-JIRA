function Plan() {
    this.id = 'plan';
    this.getName = function() { return AJS.$("#plan-name") };
    this.getDescription = function() { return AJS.$("#plan-description") };
    this.getTimeToComplete = function() { return AJS.$("#plan-time") };
    this.getDomains = function() { return AJS.$("#plan-domains") };
    this.getComponents = function() { return AJS.$("#plan-components") };

    this.getJson = function() {
        return '{ "name": "' + this.getName().attr("value") +
               '", "description": "' + this.getDescription().attr("value") +
               '", "time": "' + this.getTimeToComplete().attr("value") +
               '", "domains": ' + JSON.stringify(this.getDomains().val()) +
               ', "components": ' + JSON.stringify(this.getComponents().val()) +
               ' }';
    };

    this.setFromJson = function(allResources) {
        var isComponent = (allResources.length > 0 && allResources[0].hasOwnProperty('tasks'));

        if(isComponent === true) {
            this.getComponents().empty();
        } else {
            this.getDomains().empty();
        }

        for(eachKey in allResources) {
            var eachResource = allResources[eachKey];
            var eachOption = "<option>" + eachResource.name + "</option>";

            if(isComponent) {
                this.getComponents().append(eachOption);
            } else {
                this.getDomains().append(eachOption);
            }
        }
    };
};