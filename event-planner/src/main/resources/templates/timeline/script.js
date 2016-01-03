function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
    results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
};

function TaskGadgetCreator() {
    this.create = function(gadgetClass, titleText, avatarId, summaryText) {
        var taskGadget = this.createElement('DIV', gadgetClass);
        taskGadget.style.position = 'relative';
        taskGadget.id = titleText;
        var gadgetItem = this.createElement('DIV', 'dashboard-item-frame gadget-container');
        taskGadget.appendChild(gadgetItem);

        var titleElement = this.createElement('DIV', 'dashboard-item-header');
        gadgetItem.appendChild(titleElement);

        var titleTextElement = this.createElement('H3', 'dashboard-item-title');
        titleTextElement.appendChild(document.createTextNode(titleText));
        titleElement.appendChild(titleTextElement);

        var summaryElement = this.createElement('DIV', 'dashboard-item-content');
        gadgetItem.appendChild(summaryElement);

        var avatar = this.createElement('IMG');
        avatar.src = "/jira/secure/useravatar?avatarId=" + avatarId + "&amp;s=32";
        avatar.height = 32;
        avatar.width = 32;
        summaryElement.appendChild(avatar);

        var summaryTextElement = this.createElement('A');
        summaryTextElement.href = "#";
        summaryTextElement.appendChild(document.createTextNode(summaryText));
        summaryElement.appendChild(summaryTextElement);

        return taskGadget;
    };

    this.createElement = function(elementName, className) {
        var result = document.createElement(elementName);
        result.className = className;
        return result;
    };

    this.createToDo = function(titleText, avatarId, summaryText) {
         return this.create('gadget color1', titleText, avatarId, summaryText);
    };

    this.createDone = function(titleText, avatarId, summaryText) {
        return this.create('gadget color7', titleText, avatarId, summaryText);
    };

    this.createLate = function(titleText, avatarId, summaryText){
        var result = this.create('gadget', titleText, avatarId, summaryText);
        var headerElement = result.getElementsByClassName('dashboard-item-header')[0];
        headerElement.style.background = '#333333';
        return result;
    };
};

function TimeLine() {
    this.taskGadgetCreator = new TaskGadgetCreator();
    this.id = 'time-line';
    this.tasksToDoId = 'tasks-todo';
    this.datesId = 'dates';
    this.doneTasksId = 'done-tasks';
    this.getElementById = function(id) { return document.getElementById(id); };

    this.clear = function() {
        this.clearTable(this.tasksToDoId);
        this.clearTable(this.datesId);
        this.clearTable(this.doneTasksId);
    };

    this.clearTable = function(id) {
        var table = this.getElementById(id);
        while(table.cells.length > 0) {
            table.deleteCell(0);
        }
    };

    this.createTodayDateCell = function(index, date) {
        var result = this.createDateCell(index, date);
        result.style.borderLeft = '3px solid #205081';
        result.style.borderRight = '3px solid #205081';
        result.style.color = '#205081';
        return result;
    };

    this.createLateDateCell = function() {
        var result = this.createDateCell(0, "1 day late");
        result.style.color = '#d04437';
        return result;
    };

    this.createDateCell = function(index, date) {
        var dates = this.getElementById(this.datesId);
        var result = dates.insertCell(index);
        result.style.textAlign = 'center';
        result.appendChild(document.createTextNode(date));
        return result;
    };

    this.fillDates = function() {
        this.createLateDateCell();
        this.createTodayDateCell(1, "3-sty");
        this.createDateCell(2, "4-sty");
        this.createDateCell(3, "5-sty");
        this.createDateCell(4, "6-sty");
        this.createDateCell(5, "7-sty");
        this.createDateCell(6, "8-sty");
        this.createDateCell(7, "9-sty");
    };

    this.createTaskCell = function(index) {
        var tasksToDo = this.getElementById(this.tasksToDoId);
        var result = tasksToDo.insertCell(index);
        result.style.verticalAlign = 'bottom';
        result.style.padding = 0;
        result.width = '12%';
        return result;
    };

    this.createTodayTaskCell = function(index) {
        var result = this.createTaskCell(index);
        result.style.background = '#f5f5f5';
        result.style.borderLeft = '3px solid #205081';
        result.style.borderRight = '3px solid #205081';
        return result;
    };

    this.createLateTaskCell = function(index) {
        var result = this.createTaskCell(index);
        result.style.background = '#d04437';
        return result;
    };

    this.createDoneTaskCell = function(index) {
        var doneTasks = this.getElementById(this.doneTasksId);
        var result = doneTasks.insertCell(index);
        result.style.verticalAlign = 'bottom';
        result.style.padding = 0;
        result.width = '12%';
        result.style.textDecoration = 'line-through';
        return result;
    };

    this.createTodayDoneTaskCell = function(index) {
        var result = this.createDoneTaskCell(index);
        result.style.borderLeft = '3px solid #205081';
        result.style.borderRight = '3px solid #205081';
        return result;
    };

    this.createLateDoneTaskCell = function(index) {
        var result = this.createDoneTaskCell(0);
        result.style.background = '#d04437';
        return result;
    };

    this.insertMockTasks = function() {
        var lateCell = this.createLateTaskCell(0);
        lateCell.appendChild(this.taskGadgetCreator.createLate("Presentation", 10122, "Prepare pres..."));

        var todayCell = this.createTodayTaskCell(1);
        todayCell.appendChild(this.taskGadgetCreator.createToDo("Costam", 10122, "test test"));
        todayCell.appendChild(this.taskGadgetCreator.createToDo("gasd", 10122, "qwe test"));
        todayCell.appendChild(this.taskGadgetCreator.createToDo("gasdas", 10122, "gqwads test"));

        this.createTaskCell(2);
        this.createTaskCell(3);
        this.createTaskCell(4);
        this.createTaskCell(5);
        this.createTaskCell(6);

        this.createLateDoneTaskCell();

        var todayDoneCell = this.createTodayDoneTaskCell(1);
        todayDoneCell.appendChild(this.taskGadgetCreator.createDone("Costam", 10122, "test test"));

        this.createDoneTaskCell(2);
        this.createDoneTaskCell(3);
        this.createDoneTaskCell(4);
        this.createDoneTaskCell(5).appendChild(this.taskGadgetCreator.createDone("Costam", 10122, "test test"));
        this.createDoneTaskCell(6);
    };
};

var timeLine = new TimeLine();
timeLine.clear();
timeLine.fillDates();
timeLine.insertMockTasks();