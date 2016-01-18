function Category() {
    "use strict";
    var that = this;
    that.id = 'category';

    that.getName = function () {
        return AJS.$("#category-name");
    };

    that.getDescription = function () {
        return AJS.$("#category-description");
    };

    that.getJson = function () {
        return '{ "name": "' + that.getName().attr("value") +
            '", "description": "' + that.getDescription().attr("value") +
            '" }';
    };
};