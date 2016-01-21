function Category() {
    "use strict";
    var that = this;
    that.id = 'category';

    that.getName = function () {
        return AJS.$("#category-name");
    };

    that.getNameValue = function () {
        return that.getName().attr("value");
    };

    that.clear = function () {
        that.getName().val('');
    };

    that.getJson = function () {
        return '{ "name": "' + that.getNameValue() + '" }';
    };
};