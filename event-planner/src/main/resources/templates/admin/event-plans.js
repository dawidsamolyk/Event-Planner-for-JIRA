(function ($) {
    var url = AJS.contextPath() + "/rest/admin/event-types/1.0/";
 
    $(document).ready(function() {
        $.ajax({
            url: url,
            dataType: "json"
        }).done(function(config) {
            $("#name").val(config.name);
            $("#time").val(config.time);
        });
    });

})(AJS.$ || jQuery);

function updateConfig() {
  AJS.$.ajax({
    url: baseUrl + "/rest/admin/event-types/1.0/",
    type: "PUT",
    contentType: "application/json",
    data: '{ "name": "' + AJS.$("#name").attr("value") + '", "time": ' +  AJS.$("#time").attr("value") + ' }',
    processData: false
  });
}

AJS.$("#admin").submit(function(e) {
        e.preventDefault();
        updateConfig();
});