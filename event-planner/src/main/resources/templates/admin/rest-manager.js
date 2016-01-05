function RESTManager() {
    this.baseUrl = AJS.contextPath() + "/rest/event-plans/1.0/";

    this.post = function(resource) {
        if(resource === undefined) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot save undefined resource!'
                });
                return;
        }
        if(!resource.id || 0 === resource.id.length) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot save resource with empty name!'
                });
                return;
        }
        if(!resource.getJson() || 0 === resource.getJson().length) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot save empty resource (without data)!'
                });
                return;
        }

        jQuery.ajax({
            url: this.baseUrl + resource.id,
            type: "POST",
            contentType: "application/json",
            data: resource.getJson(),
            processData: false,
            error: function (request, status, error) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot save ' + resource.id + '!',
                    body: 'Status: ' + status
                });
            }
        });
    };

    this.get = function(sourceId, destinationResource) {
        if(!sourceId || 0 === sourceId.length) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot get resource with empty name!'
                });
                return;
        }
        if(destinationResource === undefined) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot save data into undefined resource!'
                });
                return;
        }

        jQuery.ajax({
            url: this.baseUrl + sourceId,
            type: "GET",
            dataType: "json",
            success: function (data, status, request) {
                destinationResource.setFromJson(data);
            },
            error: function (request, status, error) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot get ' + sourceId + '!',
                    body: 'Status: ' + status
                });
            }
        });
    };

    this.getAsPost = function(sourceId, destinationResource, objectId) {
        if(!sourceId || 0 === sourceId.length) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot get resource with empty name!'
                });
                return;
        }
        if(destinationResource === undefined) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot save data into undefined resource!'
                });
                return;
        }
        if(!objectId || 0 === objectId.length) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot get object with empty ID!'
                });
                return;
        }

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
                    AJS.flag({
                        type: 'error',
                        title: 'Cannot get ' + sourceId + '!',
                        body: 'Status: ' + status
                    });
                }
            });
    };

    this.doDelete = function(resourceId, objectId) {
        if(!resourceId || 0 === resourceId.length) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot delete resource with empty name!'
                });
                return;
        }
        if(!objectId || 0 === objectId.length) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot delete object with empty ID!'
                });
                return;
        }

        jQuery.ajax({
            url: this.baseUrl + resourceId,
            type: "DELETE",
            contentType: "text/plain",
            data: objectId,
            success: function (data, status, request) {
                AJS.flag({
                    type: 'success',
                    title: 'Resource ' + resourceId + ' was removed successfully!'
                });
            },
            error: function (request, status, error) {
                    AJS.flag({
                        type: 'error',
                        title: 'Cannot delete ' + resourceId + '!',
                        body: 'Status: ' + status
                    });
            }
        });
    }

    this.getIssues = function(projectKey, timeLine) {
        if(!projectKey || 0 === projectKey.length) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot get Issues for empty project key!'
                });
                return;
        }
        if(timeLine === undefined) {
                AJS.flag({
                    type: 'error',
                    title: 'Time Line object is undefined!'
                });
                return;
        }

        jQuery.ajax({
            url: this.baseUrl + "issues?project-key=" + projectKey,
            type: "GET",
            dataType: "json",
            success: function (data, status, request) {
                timeLine.issues = data;
                timeLine.refresh();
            },
            error: function (request, status, error) {
                AJS.flag({
                    type: 'error',
                    title: 'Error while getting Issues for project ' + projectKey + '!',
                    body: 'Status: ' + status
                });
            }
        });

    }

    this.getProjectDueDate = function(projectKey, timeLine) {
        if(!projectKey || 0 === projectKey.length) {
                AJS.flag({
                    type: 'error',
                    title: 'Cannot get Project Due Date for empty project key!'
                });
                return;
        }
        if(timeLine === undefined) {
                AJS.flag({
                    type: 'error',
                    title: 'Time Line object is undefined!'
                });
                return;
        }

        jQuery.ajax({
            url: this.baseUrl + "project/release-date?project-key=" + projectKey,
            type: "GET",
            dataType: "json",
            success: function (data, status, request) {
                var projectDueDate = new Date(data);
                timeLine.setDeadlineDate(projectDueDate);
                timeLine.markDeadlineDateCells();
            },
            error: function (request, status, error) {
                    AJS.flag({
                        type: 'error',
                        title: 'Cannot get project Due Date for project ' + projectKey + '!',
                        body: 'Status: ' + status
                    });
            }
        });
    }
};