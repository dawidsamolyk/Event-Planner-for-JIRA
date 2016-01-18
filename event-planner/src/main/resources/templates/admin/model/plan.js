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

    that.getCategories = function () {
        return AJS.$("#plan-categories");
    };

    that.getComponents = function () {
        return AJS.$("#plan-components");
    };

    that.getJson = function () {
        var name, description, categoriesNamesArray, componentsNamesArray;

        name = that.getName().attr("value");
        description = that.getDescription().attr("value");
        categoriesNamesArray = JSON.stringify(that.getCategories().val());
        componentsNamesArray = JSON.stringify(that.getComponents().val());

        return '{ "name": "' + name +
            '", "description": "' + description +
            ', "categoriesNames": ' + categoriesNamesArray +
            ', "componentsNames": ' + componentsNamesArray +
            ' }';
    };

    that.setFromJson = function (resources) {
        if (resources.length === 0) {
            return true;
        }
        var inputResourceIsComponent, inputResourceIsPlan, eachKey, eachName, eachElement;
        inputResourceIsComponent = resources.length > 0 && resources[0].hasOwnProperty('tasksNames');
        inputResourceIsPlan = resources.length > 0 && resources[0].hasOwnProperty('categoriesNames') && resources[0].hasOwnProperty('componentsNames');

        if (inputResourceIsPlan === true) {
            return that.setFromPlanObject(resources);
        }

        if (inputResourceIsComponent === true) {
            that.getComponents().empty();
        } else {
            that.getCategories().empty();
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
                that.getCategories().append(eachElement);
            }
        }
        return true;
    };

    that.getElement = function (value) {
        return "<".concat(that.elementsTag).concat(">").concat(value).concat("</").concat(that.elementsTag).concat(">");
    };

    that.setFromPlanObject = function (plans) {
        var plan = plans[0];

        if (plan.name === undefined || plan.description === undefined) {
            return false;
        }

        that.getName().append(plan.name);
        that.getDescription().append(plan.description);

        if (that.setCategoriesNames(plan.categoriesNames) === false) {
            return false;
        }
        if (that.setComponentsNames(plan.componentsNames) === false) {
            return false;
        }
        return true;
    };

    that.setCategoriesNames = function (categoriesNames) {
        var eachKey, categoryName;

        for (eachKey in categoriesNames) {
            categoryName = categoriesNames[eachKey];

            if (categoryName === undefined) {
                return false;
            }

            that.getCategories().append(that.getElement(categoryName));
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