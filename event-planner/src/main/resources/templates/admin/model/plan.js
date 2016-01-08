function Plan() {
    var that = this;
    that.id = 'plan';
    that.elementsTag = 'option';
    that.getName = function() { return AJS.$("#plan-name") };
    that.getDescription = function() { return AJS.$("#plan-description") };
    that.getTimeToComplete = function() { return AJS.$("#plan-time") };
    that.getDomains = function() { return AJS.$("#plan-domains") };
    that.getComponents = function() { return AJS.$("#plan-components") };

    that.getJson = function() {
        return '{ "name": "' + that.getName().attr("value") +
               '", "description": "' + that.getDescription().attr("value") +
               '", "time": ' + that.getTimeToComplete().attr("value") +
               ', "domains": ' + JSON.stringify(that.getDomains().val()) +
               ', "components": ' + JSON.stringify(that.getComponents().val()) +
               ' }';
    };

    that.setFromJson = function(resources) {
        if(resources.length === 0) {
            return;
        }
        var isComponent = (resources.length > 0 && resources[0].hasOwnProperty('tasks'));
        var isPlan = (resources.length > 0 && resources[0].hasOwnProperty('domains') && resources[0].hasOwnProperty('components'));

        if(isPlan === true) {
            that.setFromPlanObject(resources);
            return;
        }

        if(isComponent === true) {
            that.getComponents().empty();
        } else {
            that.getDomains().empty();
        }

        for(eachKey in resources) {
            var eachElement = that.getElement(resources[eachKey].name);

            if(isComponent) {
                that.getComponents().append(eachElement);
            } else {
                that.getDomains().append(eachElement);
            }
        }
    };

    that.getElement = function(value) {
        return "<".concat(that.elementsTag).concat(">").concat(value).concat("</").concat(that.elementsTag).concat(">");
    };

    that.setFromPlanObject = function(plans) {
        var plan = plans[0];

        that.getName().append(plan.name);
        that.getDescription().append(plan.description);
        that.getTimeToComplete().append(plan.time);

        for(eachKey in plan.domains) {
            that.getDomains().append(that.getElement(plan.domains[eachKey]));
        }
        for(eachKey in plan.components) {
            that.getComponents().append(that.getElement(plan.components[eachKey]));
        }
    };
};