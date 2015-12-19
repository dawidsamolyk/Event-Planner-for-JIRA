function Plan() {
    this.id = 'plan';
    this.elementsTag = 'option';
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

    this.setFromJson = function(resources) {
        var isComponent = (resources.length > 0 && resources[0].hasOwnProperty('tasks'));
        var isPlan = (resources.length > 0 && resources[0].hasOwnProperty('domains') && resources[0].hasOwnProperty('components'));

        if(isPlan === true) {
            this.setFromPlanObject(resources);
            return;
        }

        if(isComponent === true) {
            this.getComponents().empty();
        } else {
            this.getDomains().empty();
        }

        for(eachKey in resources) {
            var eachElement = this.getElement(resources[eachKey].name);

            if(isComponent) {
                this.getComponents().append(eachElement);
            } else {
                this.getDomains().append(eachElement);
            }
        }
    };

    this.getElement = function(value) {
        return "<".concat(this.elementsTag).concat(">").concat(value).concat("</").concat(this.elementsTag).concat(">");
    };

    this.setFromPlanObject = function(plans) {
        var plan = plans[0];

        this.getName().append(plan.name);
        this.getDescription().append(plan.description);
        this.getTimeToComplete().append(plan.time);

        for(eachKey in plan.domains) {
            this.getDomains().append(this.getElement(plan.domains[eachKey]));
        }
        for(eachKey in plan.components) {
            this.getComponents().append(this.getElement(plan.components[eachKey]));
        }
    };
};