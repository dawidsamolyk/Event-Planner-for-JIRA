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

function TimeLineDateCreator(datesId) {
    this.datesId = datesId;
    this.getElementById = function(id) { return document.getElementById(id); };

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

    this.setNextDayAndGetDateString = function(date) {
        date.setDate(date.getDate() + 1);
        return date.toDateString();
    };
};

function TimeLineTasksCreator(tasksToDoId, doneTasksId) {
    this.tasksToDoId = tasksToDoId;
    this.doneTasksId = doneTasksId;
    this.getElementById = function(id) { return document.getElementById(id); };

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
};

function TimeLine() {
    this.id = 'time-line';
    this.tasksToDoId = 'tasks-todo';
    this.datesId = 'dates';
    this.doneTasksId = 'done-tasks';
    this.datesCreator = new TimeLineDateCreator(this.datesId);
    this.tasksCreator = new TimeLineTasksCreator(this.tasksToDoId, this.doneTasksId);
    this.taskGadgetCreator = new TaskGadgetCreator();
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

    this.fillLate = function() {
        this.datesCreator.createLateDateCell();

        var lateCell = this.tasksCreator.createLateTaskCell(0);
        lateCell.appendChild(this.taskGadgetCreator.createLate("Presentation", 10122, "Prepare pres..."));

        this.tasksCreator.createLateDoneTaskCell();
    };

    this.fillToday = function(currentDate) {
        this.datesCreator.createTodayDateCell(1, currentDate.toDateString());

        var todayCell = this.tasksCreator.createTodayTaskCell(1);
        todayCell.appendChild(this.taskGadgetCreator.createToDo("Costam", 10122, "test test"));
        todayCell.appendChild(this.taskGadgetCreator.createToDo("gasd", 10122, "qwe test"));
        todayCell.appendChild(this.taskGadgetCreator.createToDo("gasdas", 10122, "gqwads test"));

        var todayDoneCell = this.tasksCreator.createTodayDoneTaskCell(1);
        todayDoneCell.appendChild(this.taskGadgetCreator.createDone("Costam", 10122, "test test"));
    };

    this.fillNextDays = function(currentDate, numberOfNextDays) {
        for(index = 2; index < numberOfNextDays + 2; index++) {
            this.datesCreator.createDateCell(index, this.datesCreator.setNextDayAndGetDateString(currentDate));

            this.tasksCreator.createTaskCell(index);

            this.tasksCreator.createDoneTaskCell(index);
        }
    };
};

var currentDate = new Date();

var timeLine = new TimeLine();
timeLine.clear();
timeLine.fillLate();
timeLine.fillToday(currentDate);
timeLine.fillNextDays(currentDate, 6);