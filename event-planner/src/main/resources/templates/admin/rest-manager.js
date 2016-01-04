function RESTManager() {
    this.baseUrl = AJS.contextPath() + "/rest/event-plans/1.0/";

    this.post = function(resource) {
        jQuery.ajax({
            url: this.baseUrl + resource.id,
            type: "POST",
            contentType: "application/json",
            data: resource.getJson(),
            processData: false,
            error: function (request, status, error) {
                // Wyświetlenie komunikatu o błędzie
            }
        });
    };

    this.get = function(sourceId, destinationResource) {
        jQuery.ajax({
            url: this.baseUrl + sourceId,
            type: "GET",
            dataType: "json",
            success: function (data, status, request) {
                destinationResource.setFromJson(data);
            },
            error: function (request, status, error) {
                // Wyświetlenie komunikatu o błędzie
            }
        });
    };

    this.getAsPost = function(sourceId, destinationResource, objectId) {
            jQuery.ajax({
                url: this.baseUrl + sourceId,
                type: "POST",
                dataType: "json",
                contentType: "text/plain",
                data: objectId,
                success: function (data, status, request) {
                    destinationResource.setFromJson(data);
                },
                error: function (request, status, error) {
                    // Wyświetlenie komunikatu o błędzie
                }
            });
        };

    this.doDelete = function(resourceId, objectId) {
        jQuery.ajax({
            url: this.baseUrl + resourceId,
            type: "DELETE",
            contentType: "text/plain",
            data: objectId,
            success: function (data, status, request) {
                // Wyświetlenie komunikatu o sukcesie
                console.log("DELETE Success");
            },
            error: function (request, status, error) {
                // Wyświetlenie komunikatu o błędzie
            }
        });
    }

    this.getIssues = function(projectKey, timeLine) {
        jQuery.ajax({
            url: this.baseUrl + "issues?project-key=" + projectKey,
            type: "GET",
            dataType: "json",
            success: function (data, status, request) {
                timeLine.fill(data);
            },
            error: function (request, status, error) {
                // Wyświetlenie komunikatu o błędzie
            }
        });
    }
};