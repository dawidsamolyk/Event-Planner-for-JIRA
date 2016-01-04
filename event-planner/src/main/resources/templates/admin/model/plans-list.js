function PlansList() {
    this.id = 'plans-table-body';
    this.getTable = function() { return document.getElementById(this.id) };

    this.setFromJson = function(allPlans) {
        this.clearTable();

        for(eachKey in allPlans) {
            var eachPlan = allPlans[eachKey];
            this.insert(eachPlan);
        }
    };

    this.clearTable = function() {
        var table = this.getTable();
        while(table.rows.length > 0) {
            table.deleteRow(0);
        }
    };

    this.insert = function(plan) {
        var contextPath = AJS.contextPath();

        var table = this.getTable();
        var newRow = table.insertRow(table.rows.length);

        var nameSpan = this.createDiv(plan.name, "field-name");
        var name = this.createLink(nameSpan, contextPath + "/secure/ViewEventOrganizationPlan.jspa?id=" + plan.id, "View Event Organization Plan");
        var description = this.createDiv(plan.description, "description secondary-text");

        var nameAndDescriptionCell = this.insertCell(newRow, 0);
        nameAndDescriptionCell.appendChild(name);
        nameAndDescriptionCell.appendChild(description);

        this.insertCell(newRow, 1).appendChild(this.createListFrom(plan.domains));
        this.insertCell(newRow, 2).appendChild(this.createListFrom(plan.components));
        this.insertCell(newRow, 3).appendChild(document.createTextNode(plan.time));

        // TODO odkomentuj dopiero, gdy zostanie zaimplementowana akacja edycji planu eventu
        //var editLink = this.createLink(document.createTextNode('Edit'), contextPath + "/secure/EditEventOrganizationPlan.jspa?id=" + plan.id, "Edit Event Organization Plan", "edit-plan");
        var deleteLink = this.createLink(document.createTextNode('Delete'), contextPath + "/secure/DeleteEventOrganizationPlan.jspa?id=" + plan.id, "Delete Event Organization Plan", "delete-plan");

        var operationsList = this.createList();
            operationsList.className = "operations-list";
            // TODO odkomentuj dopiero, gdy zostanie zaimplementowana akacja edycji planu eventu
            //this.addToList(operationsList, editLink);
            this.addToList(operationsList, deleteLink);

        var operationsCell = this.insertCell(newRow, 4);
        operationsCell.appendChild(operationsList);

        return newRow;
    };

    this.insertCell = function(row, index) {
        return row.insertCell(index);
    };

    this.createLink = function(element, href, title, id) {
        var result = document.createElement('A');
        result.href = href;
        result.title = title;
        result.id = id;
        result.appendChild(element);
        return result;
    }

    this.createDiv = function(text, className) {
        var result = document.createElement('DIV');
        result.className = className;
        result.appendChild(document.createTextNode(text));
        return result;
    };

    this.createList = function() {
        return document.createElement('UL');
    };

    this.addToList = function(list, element) {
        var eachListElement = document.createElement('LI');
        eachListElement.appendChild(element);
        list.appendChild(eachListElement);
        return list;
    };

    this.createListFrom = function(array) {
        var result = this.createList();
        for(eachKey in array) {
            this.addToList(result, document.createTextNode(array[eachKey]));
        }
        return result;
    };
};