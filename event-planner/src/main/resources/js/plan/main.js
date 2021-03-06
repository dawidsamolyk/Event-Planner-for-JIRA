var plansList = new PlansList(), category = new Category(), rest = new RESTManager();

function loadPlansList() {
    "use strict";
    rest.get('plan', plansList);
}

AJS.$(document).ready(
    function () {
        "use strict";
        loadPlansList();
    }
);

function isListContainsText(listId, text) {
    "use strict";
    return AJS.$(listId).children('li:contains("' + text + '")').length > 0;
}

function savePlan() {
    "use strict";
    var projectKey, name, description, reserveTime, categories = [];

    projectKey = jQuery('#source-project').children(":selected").attr("id");
    name = jQuery('#plan-name').attr('value');
    description = jQuery('#plan-description').attr('value');
    reserveTime = jQuery('#plan-reserve-time').attr('value');

    if (!reserveTime || reserveTime.length === 0) {
        reserveTime = "0";
    }

    jQuery("#selected-category li").each(
        function () {
            categories.push(jQuery(this).text());
        }
    );

    rest.putPlan(projectKey, name, description, reserveTime, categories);
}

function addListeners() {
    "use strict";
    if (AJS.$("#source-project").children().length === 0) {
        AJS.$('#add-plan-button').remove();
    }

    AJS.$('#event-category-add-button').click(
        function (event) {
            event.preventDefault();
            var nameValue = category.getNameValue();

            if (isListContainsText("#selected-category", nameValue) || isListContainsText("#available-category", nameValue)) {
                jQuery('#category-name-description').html('<div class="error" id="category-name-description">Cannot add duplicate Category! Max. 80 characters.</div>');

            } else if (nameValue && nameValue.length > 0) {
                AJS.$("#selected-category").append("<li class='aui-label aui-label-closeable event-plan-list-element' style='padding-right: 5px;'>" + nameValue + "<span tabindex='0' class='aui-icon aui-icon-close'></span></li>");
                AJS.$('#category-name').val('');

                jQuery(".aui-icon-close").click(
                    function (event) {
                        event.srcElement.parentNode.remove();
                    }
                );
                jQuery('#category-name-description').html('<div class="description" id="category-name-description">Max. 80 characters.</div>');

            } else {
                jQuery('#category-name-description').html('<div class="error" id="category-name-description">Name cannot be empty! Max. 80 characters.</div>');
            }
        }
    );

    AJS.$('#event-plan-dialog-create-button').click(
        function (event) {
            "use strict";
            event.preventDefault();
            var planName = AJS.$("#plan-name").attr('value'), selectedCategories = AJS.$("#selected-category");

            if (planName && planName.length > 0) {
                jQuery('#plan-name-description').html('<div class="description" id="plan-name-description">Max. 80 characters.</div>');
            } else {
                jQuery('#plan-name-description').html('<div class="error" id="plan-name-description">Name cannot be empty! Max. 80 characters.</div>');
            }

            if (selectedCategories.children().length > 0) {
                jQuery('#plan-categories-description').html('<div class="description" id="plan-categories-description">Drag and drop Categories which best describe this template. Select at least one.</div>');
            } else {
                jQuery('#plan-categories-description').html('<div class="error" id="plan-categories-description">Drag and drop at least one Category which best describe this template!</div>');
            }

            if (planName && planName.length > 0 && selectedCategories.children().length > 0) {
                savePlan();
                AJS.dialog2('#event-plan-dialog').hide();
            }
        }
    );

    AJS.$("#add-plan-button").click(
        function (e) {
            e.preventDefault();
            document.getElementById('new-plan-configuration').reset();
            jQuery('#selected-category').empty();
            rest.get(category.id, category);

            jQuery('#category-name-description').html('<div class="description" id="category-name-description">Max. 80 characters.</div>');
            jQuery('#plan-categories-description').html('<div class="description" id="plan-categories-description">Drag and drop Categories which best describe this template. Select at least one.</div>');
            jQuery('#category-name-description').html('<div class="description" id="category-name-description">Max. 80 characters.</div>');

            AJS.dialog2('#event-plan-dialog').show();
        }
    );

    AJS.$("#event-plan-dialog-cancel-button").click(
        function (e) {
            e.preventDefault();
            AJS.dialog2('#event-plan-dialog').hide();
        }
    );

    AJS.$("#import-plans-button").click(
        function (e) {
            e.preventDefault();
            document.getElementById("import-event-plan-template-form").reset();
            AJS.dialog2('#import-plan-dialog').show();
        }
    );

    AJS.$("#import-plan-templates-cancel").click(
        function (e) {
            e.preventDefault();
            AJS.dialog2('#import-plan-dialog').hide();
        }
    );

    AJS.$("#import-plan-templates-submit").click(
        function (e) {
            e.preventDefault();

            var file = document.getElementById("uploadFile").files[0], reader = new FileReader();

            reader.readAsText(file, "UTF-8");
            reader.onload = function (event) {
                jQuery.ajax({
                    url: AJS.contextPath() + "/rest/event-plans/1.0/plan/import",
                    type: "POST",
                    contentType: "text/plain",
                    data: event.target.result,
                    success: function () {
                        require('aui/flag')({
                            type: 'success',
                            title: 'Event Plan Template imported successfully!',
                            close: "auto"
                        });
                        loadPlansList();
                    },
                    error: function (request) {
                        require('aui/flag')({
                            type: 'error',
                            title: 'Cannot import selected Event Plan Template!',
                            body: 'Status: ' + request.statusText,
                            close: "auto"
                        });
                    }
                });
            };
            reader.onerror = function () {
                require('aui/flag')({
                    type: 'error',
                    title: 'Error while reading XML file!',
                    close: "auto"
                });
            };

            AJS.dialog2('#import-plan-dialog').hide();
        }
    );

    jQuery("ul.connectedSortable").sortable({
        connectWith: "ul",
        dropOnEmpty: true,
        cursor: "move"
    }).disableSelection();
}




