function Domain() {
    var that = this;
    that.id = 'domain';
    that.getName = function() { return AJS.$("#domain-name") };
    that.getDescription = function() { return AJS.$("#domain-description") };

    that.getJson = function() {
        return '{ "name": "' + that.getName().attr("value") +
               '", "description": "' + that.getDescription().attr("value") +
               '" }';
    };
};