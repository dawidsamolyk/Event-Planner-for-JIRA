function RESTManager() {
    "use strict";
    var that = this;

    that.baseUrl = AJS.contextPath() + "/rest/event-plans/1.0/";

    that.get = function (sourceId, destinationResource) {
        if (!sourceId || 0 === sourceId.length) {
            require('aui/flag')({
                type: 'error',
                title: 'Cannot get resource with empty name!'
            });
            return;
        }
        if (destinationResource === undefined) {
            require('aui/flag')({
                type: 'error',
                title: 'Cannot save data into undefined resource!'
            });
            return;
        }

        jQuery.ajax({
            url: that.baseUrl + sourceId,
            type: "GET",
            dataType: "json",
            success: function (data) {
                destinationResource.setFromJson(data);
            },
            error: function (request) {
                require('aui/flag')({
                    type: 'error',
                    title: 'Cannot get ' + sourceId + '!',
                    body: 'Status: ' + request.statusText
                });
            }
        });
    };

    that.postPlan = function (projectKey, name, description, reserveTime, categories) {
        jQuery.ajax({
            url: that.baseUrl + 'plan',
            type: "POST",
            contentType: "application/json",
            data: '{ "projectKey": "' + projectKey + '", "name": "' + name + '", "description": "' + description + '", "reserveTime": "' + reserveTime + '", "categories": ' + JSON.stringify(categories) + ' }',
            processData: false,
            success: function () {
                require('aui/flag')({
                    type: 'success',
                    title: 'Plan template named ' + name + ' saved successfully!',
                    close: 'auto'
                });
            },
            error: function (request, status, error) {
                var flagBody, flagTitle = 'Error while saving Plan template named ' + name + '!';

                if (error.valueOf() === 'Precondition Failed') {
                    flagTitle = 'Plan template ' + name + ' is not fullfilled!';

                } else if (error.valueOf() === 'Not Found') {
                    flagTitle = 'Project with key ' + projectKey + ' not found!';

                } else if (error.valueOf() === 'Not Acceptable') {
                    flagTitle = 'Not found any tasks or Event deadline Version in project with key ' + projectKey + '!';
                }

                require('aui/flag')({
                    type: 'error',
                    title: flagTitle,
                    body: flagBody,
                    close: 'auto'
                });
            },
            complete: function () {
                loadPlansList();
            }
        });
    };

    that.doDelete = function (resourceId, objectId) {
        if (!resourceId || 0 === resourceId.length) {
            require('aui/flag')({
                type: 'error',
                title: 'Cannot delete resource with empty name!'
            });
            return;
        }
        if (!objectId || 0 === objectId.length) {
            require('aui/flag')({
                type: 'error',
                title: 'Cannot delete object with empty ID!'
            });
            return;
        }

        jQuery.ajax({
            url: that.baseUrl + resourceId,
            type: "DELETE",
            contentType: "text/plain",
            data: objectId,
            success: function (data) {
                require('aui/flag')({
                    type: 'success',
                    title: 'Resource ' + resourceId + ' was removed successfully!'
                });
            },
            error: function (request) {
                console.error(request.responseText);
                require('aui/flag')({
                    type: 'error',
                    title: 'Cannot delete ' + resourceId + '!',
                    body: 'Status: ' + request.statusText
                });
            }
        });
    };

    that.getIssues = function (projectKey, timeLine) {
        if (!projectKey || 0 === projectKey.length) {
            require('aui/flag')({
                type: 'error',
                title: 'Cannot get Issues for empty project key!'
            });
            return;
        }
        if (timeLine === undefined) {
            require('aui/flag')({
                type: 'error',
                title: 'Time Line object is undefined!'
            });
            return;
        }

        jQuery.ajax({
            url: that.baseUrl + "issues?project-key=" + projectKey,
            type: "GET",
            dataType: "json",
            success: function (data, status, request) {
                if (request.statusText.valueOf() === 'No Content') {
                    require('aui/flag')({
                        type: 'info',
                        title: 'No Tasks for project ' + projectKey + '.'
                    });
                }
                timeLine.setIssues(data);
            },
            error: function (request, status, error) {
                console.error(request.responseText);
                var flagTitle = 'Error (' + error + ') while getting Tasks for Project ' + projectKey + '!';

                if (error.valueOf() === 'Not Found') {
                    flagTitle = 'Project ' + projectKey + ' not found.';
                }

                require('aui/flag')({
                    type: 'error',
                    title: flagTitle
                });
            }
        });
    };

    that.getProjectDeadline = function (projectKey, timeLine) {
        if (!projectKey || 0 === projectKey.length) {
            require('aui/flag')({
                type: 'error',
                title: 'Cannot get Project Due Date for empty project key!'
            });
            return;
        }
        if (timeLine === undefined) {
            require('aui/flag')({
                type: 'error',
                title: 'Time Line object is undefined!'
            });
            return;
        }

        jQuery.ajax({
            url: that.baseUrl + "project/release-date?project-key=" + projectKey,
            type: "GET",
            dataType: "json",
            success: function (data) {
                var projectDeadline = new Date(data);
                timeLine.setProjectDeadline(projectDeadline);
            },
            error: function (request, status, error) {
                console.error(request.responseText);
                require('aui/flag')({
                    type: 'error',
                    title: 'Deadline for project ' + projectKey + ' is not set!',
                    body: 'Cannot show project Time Line.'
                });
            }
        });
    };

    that.postTask = function (taskKey, targetStatus, targetDueDate, timeLine) {
        if (taskKey === undefined || !taskKey || 0 === taskKey.length) {
            require('aui/flag')({
                type: 'error',
                title: 'Cannot modify Task with empty Key!'
            });
            return;
        }
        if (targetStatus === undefined || !targetStatus || 0 === targetStatus.length) {
            require('aui/flag')({
                type: 'error',
                title: 'Cannot modify Task to unknown status!'
            });
            return;
        }
        if (targetDueDate === undefined || !targetDueDate || 0 === targetDueDate.length) {
            require('aui/flag')({
                type: 'error',
                title: 'Cannot set Task to unknown due date!'
            });
            return;
        }

        var targetDueDateTime;
        if (targetDueDate === 'late') {
            targetDueDateTime = -1;
        } else {
            targetDueDateTime = new Date(targetDueDate).getTime();
        }

        jQuery.ajax({
            url: that.baseUrl + "issue/modify",
            type: "POST",
            contentType: "application/json",
            data: '{ "key": "' + taskKey + '", "status": "' + targetStatus + '", "dueDateTime": "' + targetDueDateTime + '" }',
            processData: false,
            success: function (data) {
                if (targetDueDate === null) {
                    targetDueDate = new Date();
                    targetDueDate.setDate(new Date().getDate() - 1);
                } else {
                    targetDueDate = new Date(targetDueDate);
                }

                require('aui/flag')({
                    type: 'success',
                    title: 'Task ' + taskKey + ' modified successfully!',
                    body: '<ul><li>Status: ' + targetStatus + '</li><li>Due Date: ' + targetDueDate.toDateString() + '</li></ul>',
                    close: 'auto'
                });
            },
            error: function (request, status, error) {
                var flagBody, flagTitle = 'Error while modifying Task with key ' + taskKey + '!';

                if (error.valueOf() === 'Forbidden') {
                    flagTitle = 'Cannot close ' + taskKey + '!';
                    flagBody = "Probable issue: blocking Sub-Tasks.<br><a href='" + AJS.contextPath() + "/browse/" + taskKey + "'>Show Task.</a>";

                } else if (error.valueOf() === 'Not Found') {
                    flagTitle = 'Task ' + taskKey + ' not found!';
                    flagBody = 'Try again or contact with plugin author.';

                } else if (error.valueOf() === 'Conflict') {
                    flagTitle = 'Task ' + taskKey + ' not modified because of internal conflict!';
                    flagBody = 'Try again or contact with plugin author.';
                }

                require('aui/flag')({
                    type: 'error',
                    title: flagTitle,
                    body: flagBody,
                    close: 'auto'
                });
            },
            complete: function () {
                if (timeLine !== undefined) {
                    timeLine.refresh();
                }
            }
        });
    };
}