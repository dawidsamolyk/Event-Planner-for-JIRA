function Category() {
    "use strict";
    var that = this;
    that.id = 'category';

    that.getNameField = function () {
        return AJS.$("#" + that.id + "-name");
    };

    that.getNameValue = function () {
        return that.getNameField().attr("value");
    };

    that.clear = function () {
        that.getNameField().val('');
    };

    that.getJson = function () {
        // TODO wszystkie wybrane kategorie w formie tablicy JSON
        return '{ "name": "' + that.getNameValue() + '" }';
    };

    that.getAvailableCategories = function () {
        return AJS.$("#available-" + that.id);
    };

    that.getSelectedCategories = function () {
        return AJS.$("#selected-" + that.id);
    };

    that.setFromJson = function (data) {
        var eachKey, category, availableCategories;
        availableCategories = that.getAvailableCategories();
        availableCategories.empty();

        for (eachKey in data) {
            category = data[eachKey];

            if (that.isFullFilled(category) === false) {
                return false;
            }
            availableCategories.append("<li class='ui-state-default aui-label event-plan-list-element'>" + category.name + "</li>");
        }

        return true;
    };

    that.isFullFilled = function (category) {
        return category !== undefined && category.name !== undefined;
    };
}