function PlansList() {
    "use strict";
    var that = this;

    that.id = 'plans-table-body';

    that.getTable = function () {
        return document.getElementById(that.id);
    };

    that.setFromJson = function (allPlans) {
        var eachKey;
        that.clearTable();

        for (eachKey in allPlans) {
            that.insert(allPlans[eachKey]);
        }
        return true;
    };

    that.clearTable = function () {
        var table = that.getTable();
        while (table.rows.length > 0) {
            table.deleteRow(0);
        }
    };

    that.insert = function (plan) {
        if (plan.name === undefined || plan.description === undefined) {
            return false;
        }
        var table, newRow;

        table = that.getTable();
        newRow = table.insertRow(table.rows.length);

        that.insertNameAndDescriptionCell(plan, newRow);
        that.insertStatistics(plan, newRow);
        that.insertList(plan.component, true, newRow);
        that.insertList(plan.eventCategory, false, newRow);
        that.insertOperationsLinksCell(plan, newRow);

        return true;
    };

    that.insertNameAndDescriptionCell = function (plan, row) {
        var nameSpan, name, description, nameAndDescriptionCell;

        nameSpan = that.createDiv(plan.name, "field-name");
        name = that.createLink(nameSpan, AJS.contextPath() + "/secure/ViewEventPlanTemplate.jspa?id=".concat(plan.id), "View Event Plan template");
        description = that.createDiv(plan.description, "description secondary-text");

        nameAndDescriptionCell = that.insertCell(row, 0);
        nameAndDescriptionCell.appendChild(name);
        nameAndDescriptionCell.appendChild(description);
    };

    that.insertList = function (array, countTasks, row) {
        var list = that.createListFrom(array, countTasks);
        that.insertCell(row, row.length).appendChild(list);
    };

    that.insertStatistics = function (plan, row) {
        var list = that.createList();

        that.addToList(list, document.createTextNode('Optimistic: ' + that.getEstimatedTimeToCompleteText(plan.optimisticTimeToComplete)));
        that.addToList(list, document.createTextNode('The worst: ' + that.getEstimatedTimeToCompleteText(plan.theWorstTimeToComplete)));
        that.addToList(list, document.createTextNode('Time reserve: ' + that.getEstimatedTimeToCompleteText(plan.reserveTimeInDays)));

        that.insertCell(row, row.length).appendChild(list);
    };

    that.getEstimatedTimeToCompleteText = function (numberOfDays) {
        var months = Math.floor(numberOfDays / 30), days = numberOfDays % 30, result = "~";

        if (numberOfDays === 0) {
            return "none";
        }
        if (months === 1) {
            result += months + ' month';
        } else if (months > 1) {
            result += months + ' months';
        }
        if (months > 0 && days > 0) {
            result += ' and ';
        }
        if (days === 1) {
            result += days + ' day';
        } else if (days > 1) {
            result += days + ' days';
        }

        return result;
    };

    that.insertOperationsLinksCell = function (plan, row) {
        var exportLink, deleteLink, operationsList, operationsCell;

        exportLink = that.createLink(document.createTextNode('Export'), "#", "Export Event Plan template", "export-plan-".concat(plan.id));
        deleteLink = that.createLink(document.createTextNode('Delete'), AJS.contextPath() + "/secure/DeleteEventPlanTemplate.jspa?id=".concat(plan.id), "Delete Event Plan template", "delete-plan");

        operationsList = that.createList();
        operationsList.className = "operations-list";
        that.addToList(operationsList, exportLink);
        that.addToList(operationsList, deleteLink);

        operationsCell = that.insertCell(row, row.length);
        operationsCell.appendChild(operationsList);

        AJS.$('#'.concat(exportLink.id)).click(
            function (event) {
                event.preventDefault();
                var rest = new RESTManager();
                rest.exportPlan(plan);
            }
        );
    };

    that.insertCell = function (row, index) {
        return row.insertCell(index);
    };

    that.createLink = function (element, href, title, id) {
        var result = document.createElement('A');
        result.href = href;
        result.title = title;
        result.id = id;
        result.appendChild(element);
        return result;
    };

    that.createDiv = function (text, className) {
        var result = document.createElement('DIV');
        result.className = className;
        result.appendChild(document.createTextNode(text));
        return result;
    };

    that.createList = function () {
        return document.createElement('UL');
    };

    that.addToList = function (list, element) {
        var eachListElement = document.createElement('LI');
        eachListElement.appendChild(element);
        list.appendChild(eachListElement);
        return list;
    };

    that.createListFrom = function (array, countTasks) {
        var result, eachKey, eachValue, text;
        result = that.createList();

        for (eachKey in array) {
            eachValue = array[eachKey];
            if (eachValue === undefined) {
                return undefined;
            }
            if (countTasks === false) {
                text = eachValue.name;
            } else {
                text = eachValue.name.concat(' (').concat(eachValue.task.length).concat(' tasks)');
            }
            that.addToList(result, document.createTextNode(text));
        }

        return result;
    };
}