function Domain() {
    var that = this;
    that.id = 'domain';
    that.getName = function() { return AJS.$("#domain-name") };
    that.getDescription = function() { return AJS.$("#domain-description") };

    that.getJson = function() {
        var name = that.getName().attr("value");
        var description = that.getDescription().attr("value");

        return '{ "name": "' + name +
               '", "description": "' + description +
               '" }';
    };
};