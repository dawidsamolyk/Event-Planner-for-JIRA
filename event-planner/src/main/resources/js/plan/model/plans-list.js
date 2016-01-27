function PlansList() {
    "use strict";
    var that = this;

    that.id = 'plans-table-body';

    that.getTable = function () {
        return document.getElementById(that.id);
    };

    that.setFromJson = function (allPlans) {
        var eachKey, plan;
        that.clearTable();

        for (eachKey in allPlans) {
            plan = allPlans[eachKey];

            if (that.insert(plan) === false) {
                return false;
            }
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
        that.insertStatistics(plan.estimatedTimeToComplete, newRow);
        that.insertList(plan.component, newRow);
        that.insertList(plan.eventCategory, newRow);
        that.insertOperationsLinksCell(plan, newRow);

        return true;
    };

    that.insertNameAndDescriptionCell = function (plan, row) {
        var nameSpan, name, description, nameAndDescriptionCell;

        nameSpan = that.createDiv(plan.name, "field-name");
        name = that.createLink(nameSpan, AJS.contextPath() + "/secure/ViewEventOrganizationPlan.jspa?id=" + plan.id, "View Event Organization Plan");
        description = that.createDiv(plan.description, "description secondary-text");

        nameAndDescriptionCell = that.insertCell(row, 0);
        nameAndDescriptionCell.appendChild(name);
        nameAndDescriptionCell.appendChild(description);
    };

    that.insertList = function (array, row) {
        var list = that.createListFrom(array);
        that.insertCell(row, row.length).appendChild(list);
    };

    that.insertStatistics = function (estimatedTimeToComplete, row) {
        that.insertCell(row, row.length).appendChild(document.createTextNode('~' + estimatedTimeToComplete + ' day(s)'));
    };

    that.insertOperationsLinksCell = function (plan, row) {
        var deleteLink, operationsList, operationsCell;

        //TODO odkomentuj dopiero, gdy zostanie zaimplementowana akacja edycji planu eventu
        //var editLink = that.createLink(document.createTextNode('Edit'), AJS.contextPath() + "/secure/EditEventOrganizationPlan.jspa?id=" + plan.id, "Edit Event Organization Plan", "edit-plan");
        deleteLink = that.createLink(document.createTextNode('Delete'), AJS.contextPath() + "/secure/DeleteEventOrganizationPlan.jspa?id=" + plan.id, "Delete Event Organization Plan", "delete-plan");

        operationsList = that.createList();
        operationsList.className = "operations-list";
        //TODO odkomentuj dopiero, gdy zostanie zaimplementowana akacja edycji planu eventu
        //that.addToList(operationsList, editLink);
        that.addToList(operationsList, deleteLink);

        operationsCell = that.insertCell(row, row.length);
        operationsCell.appendChild(operationsList);
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

    that.createListFrom = function (array) {
        var result, eachKey, eachValue;
        result = that.createList();

        for (eachKey in array) {
            eachValue = array[eachKey];
            if (eachValue === undefined) {
                return undefined;
            }
            that.addToList(result, document.createTextNode(eachValue.name));
        }

        return result;
    };
};