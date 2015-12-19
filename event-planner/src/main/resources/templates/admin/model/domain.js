function Domain() {
    this.id = 'domain';
    this.getName = function() { return AJS.$("#domain-name") };
    this.getDescription = function() { return AJS.$("#domain-description") };

    this.getJson = function() {
        return '{ "name": "' + this.getName().attr("value") +
               '", "description": "' + this.getDescription().attr("value") +
               '" }';
    };
};