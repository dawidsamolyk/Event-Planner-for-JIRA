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
        if (plan.name === undefined || plan.description === undefined || plan.neededMonths === undefined || plan.neededDays === undefined) {
            return false;
        }
        var table, newRow, nameSpan, name, description, nameAndDescriptionCell, domainsNamesList, componentsNamesList, neededTime, deleteLink, operationsList, operationsCell;

        table = that.getTable();
        newRow = table.insertRow(table.rows.length);

        nameSpan = that.createDiv(plan.name, "field-name");
        name = that.createLink(nameSpan, AJS.contextPath() + "/secure/ViewEventOrganizationPlan.jspa?id=" + plan.id, "View Event Organization Plan");
        description = that.createDiv(plan.description, "description secondary-text");

        nameAndDescriptionCell = that.insertCell(newRow, 0);
        nameAndDescriptionCell.appendChild(name);
        nameAndDescriptionCell.appendChild(description);

        domainsNamesList = that.createListFrom(plan.domainsNames);
        if (domainsNamesList === undefined) {
            return false;
        }
        that.insertCell(newRow, 1).appendChild(domainsNamesList);

        componentsNamesList = that.createListFrom(plan.componentsNames);
        if (componentsNamesList === undefined) {
            return false;
        }
        that.insertCell(newRow, 2).appendChild(componentsNamesList);

        neededTime = '';
        if (plan.neededMonths === 1) {
            neededTime += plan.neededMonths + ' month';
        } else if (plan.neededMonths > 1) {
            neededTime += plan.neededMonths + ' months';
        }

        if (plan.neededMonths > 0 && plan.neededDays > 0) {
            neededTime += ' + ';
        }

        if (plan.neededDays === 1) {
            neededTime += plan.neededDays + ' day ';
        } else if (plan.neededDays > 1) {
            neededTime += plan.neededDays + ' days ';
        }

        that.insertCell(newRow, 3).appendChild(document.createTextNode(neededTime));

        // TODO odkomentuj dopiero, gdy zostanie zaimplementowana akacja edycji planu eventu
        //var editLink = that.createLink(document.createTextNode('Edit'), AJS.contextPath() + "/secure/EditEventOrganizationPlan.jspa?id=" + plan.id, "Edit Event Organization Plan", "edit-plan");
        deleteLink = that.createLink(document.createTextNode('Delete'), AJS.contextPath() + "/secure/DeleteEventOrganizationPlan.jspa?id=" + plan.id, "Delete Event Organization Plan", "delete-plan");

        operationsList = that.createList();
        operationsList.className = "operations-list";
        // TODO odkomentuj dopiero, gdy zostanie zaimplementowana akacja edycji planu eventu
        //that.addToList(operationsList, editLink);
        that.addToList(operationsList, deleteLink);

        operationsCell = that.insertCell(newRow, 4);
        operationsCell.appendChild(operationsList);

        return true;
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
            that.addToList(result, document.createTextNode(eachValue));
        }

        return result;
    };
};