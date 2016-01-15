function Plan() {
    "use strict";
    var that = this;

    that.id = 'plan';
    that.elementsTag = 'option';

    that.getName = function () {
        return AJS.$("#plan-name");
    };

    that.getDescription = function () {
        return AJS.$("#plan-description");
    };

    that.getNeededMonthsToComplete = function () {
        return AJS.$("#plan-needed-months");
    };

    that.getNeededDaysToComplete = function () {
        return AJS.$("#plan-needed-days");
    };

    that.getDomains = function () {
        return AJS.$("#plan-domains");
    };

    that.getComponents = function () {
        return AJS.$("#plan-components");
    };

    that.getJson = function () {
        var name, description, neededMonths, neededDays, domainsNamesArray, componentsNamesArray;

        name = that.getName().attr("value");
        description = that.getDescription().attr("value");
        neededMonths = that.getNeededMonthsToComplete().attr("value");
        neededDays = that.getNeededDaysToComplete().attr("value");
        domainsNamesArray = JSON.stringify(that.getDomains().val());
        componentsNamesArray = JSON.stringify(that.getComponents().val());

        return '{ "name": "' + name +
            '", "description": "' + description +
            '", "neededMonths": ' + neededMonths +
            ', "neededDays": ' + neededDays +
            ', "domainsNames": ' + domainsNamesArray +
            ', "componentsNames": ' + componentsNamesArray +
            ' }';
    };

    that.setFromJson = function (resources) {
        if (resources.length === 0) {
            return true;
        }
        var inputResourceIsComponent, inputResourceIsPlan, eachKey, eachName, eachElement;
        inputResourceIsComponent = resources.length > 0 && resources[0].hasOwnProperty('tasksNames');
        inputResourceIsPlan = resources.length > 0 && resources[0].hasOwnProperty('domainsNames') && resources[0].hasOwnProperty('componentsNames');

        if (inputResourceIsPlan === true) {
            return that.setFromPlanObject(resources);
        }

        if (inputResourceIsComponent === true) {
            that.getComponents().empty();
        } else {
            that.getDomains().empty();
        }

        for (eachKey in resources) {
            eachName = resources[eachKey].name;
            if (eachName === undefined) {
                return false;
            }
            eachElement = that.getElement(eachName);

            if (inputResourceIsComponent) {
                that.getComponents().append(eachElement);
            } else {
                that.getDomains().append(eachElement);
            }
        }
        return true;
    };

    that.getElement = function (value) {
        return "<".concat(that.elementsTag).concat(">").concat(value).concat("</").concat(that.elementsTag).concat(">");
    };

    that.setFromPlanObject = function (plans) {
        var plan = plans[0];

        if (plan.name === undefined || plan.description === undefined || plan.neededMonths === undefined || plan.neededDays === undefined) {
            return false;
        }

        that.getName().append(plan.name);
        that.getDescription().append(plan.description);
        that.getNeededMonthsToComplete().append(plan.neededMonths);
        that.getNeededDaysToComplete().append(plan.neededDays);

        if (that.setDomainsNames(plan.domainsNames) === false) {
            return false;
        }
        if (that.setComponentsNames(plan.componentsNames) === false) {
            return false;
        }
        return true;
    };

    that.setDomainsNames = function (domainsNames) {
        var eachKey, eachDomainName;

        for (eachKey in domainsNames) {
            eachDomainName = domainsNames[eachKey];

            if (eachDomainName === undefined) {
                return false;
            }

            that.getDomains().append(that.getElement(eachDomainName));
        }
        return true;
    };

    that.setComponentsNames = function (componentsNames) {
        var eachKey, eachComponentName;

        for (eachKey in componentsNames) {
            eachComponentName = componentsNames[eachKey];

            if (eachComponentName === undefined) {
                return false;
            }

            that.getComponents().append(that.getElement(eachComponentName));
        }
        return true;
    };
};