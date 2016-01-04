function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
    results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
};

function TaskGadgetCreator() {
    this.create = function(gadgetClass, titleText, avatarId, summaryText, issueKey, assigneeName) {
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

        var avatarLink = this.createElement('A');
        avatarLink.href = "/jira/secure/ViewProfile.jspa?name=" + assigneeName;

        var avatarImage = this.createElement('IMG');
        avatarImage.src = "/jira/secure/useravatar?avatarId=" + avatarId + "&amp;s=32";
        avatarImage.height = 32;
        avatarImage.width = 32;
        avatarLink.appendChild(avatarImage);
        summaryElement.appendChild(avatarLink);

        var summaryTextElement = this.createElement('A');
        summaryTextElement.href = AJS.contextPath() + "/browse/" + issueKey;
        summaryTextElement.appendChild(document.createTextNode(summaryText));
        summaryElement.appendChild(summaryTextElement);

        return taskGadget;
    };

    this.createElement = function(elementName, className) {
        var result = document.createElement(elementName);
        result.className = className;
        return result;
    };

    this.createToDo = function(titleText, avatarId, summaryText, issueKey, assigneeName) {
         return this.create('gadget color1', titleText, avatarId, summaryText, issueKey, assigneeName);
    };

    this.createDone = function(titleText, avatarId, summaryText, issueKey, assigneeName) {
        return this.create('gadget color7', titleText, avatarId, summaryText, issueKey, assigneeName);
    };

    this.createLate = function(titleText, avatarId, summaryText, issueKey, assigneeName){
        var result = this.create('gadget', titleText, avatarId, summaryText, issueKey, assigneeName);
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

    this.createLateDateCell = function(numberOfLateDays) {
        var result;

        if(numberOfLateDays === 0) {
            result = this.createDateCell(0, "No late");
        }
        else if(numberOfLateDays === 1) {
            result = this.createDateCell(0, "1 day late");
        }
        else {
            result = this.createDateCell(0, Math.abs(numberOfLateDays) + " days late");
        }

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
        result.style.textDecoration = 'line-through';
        return result;
    };

    this.createTodayDoneTaskCell = function(index) {
        var result = this.createDoneTaskCell(index);
        result.style.background = '#f5f5f5';
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

    this.fill = function(dataSource) {
        var lateCell = this.tasksCreator.createLateTaskCell(0);
        this.tasksCreator.createLateDoneTaskCell();
        var todayCell = this.tasksCreator.createTodayTaskCell(1);
        var todayDoneCell = this.tasksCreator.createTodayDoneTaskCell(1);

        var nextDaysCells = [];
        var nextDaysDoneCells = [];
        var numberOfNextDays = 6;
        for(index = 1; index < numberOfNextDays + 1; index++) {
            nextDaysCells[index] = this.tasksCreator.createTaskCell(index + 1);
            nextDaysDoneCells[index] = this.tasksCreator.createDoneTaskCell(index + 1);
        }

        var maximumLate = 0;

        for(eachIssueKey in dataSource) {
            var eachIssue = dataSource[eachIssueKey];
            var daysAwayFromDueDate = eachIssue.daysAwayFromDueDate;
            var componentName = eachIssue.componentsNames[0];
            var avatarId = eachIssue.avatarId;
            var summary = eachIssue.summary;
            var issueKey = eachIssue.key;
            var assigneeName = eachIssue.assigneeName;

            if(daysAwayFromDueDate > numberOfNextDays) {
                continue;
            }

            if(daysAwayFromDueDate < 0 && eachIssue.done === false) {
                lateCell.appendChild(this.taskGadgetCreator.createLate(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate == 0 && eachIssue.done === false) {
                todayCell.appendChild(this.taskGadgetCreator.createToDo(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate == 0 && eachIssue.done === true) {
                todayDoneCell.appendChild(this.taskGadgetCreator.createDone(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate > 0 && eachIssue.done === true) {
                nextDaysDoneCells[daysAwayFromDueDate].appendChild(this.taskGadgetCreator.createDone(componentName, avatarId, summary, issueKey, assigneeName));
            }
            else if(daysAwayFromDueDate > 0 && eachIssue.done === false) {
                nextDaysCells[daysAwayFromDueDate].appendChild(this.taskGadgetCreator.createToDo(componentName, avatarId, summary, issueKey, assigneeName));
            }

            if(daysAwayFromDueDate < maximumLate) {
                maximumLate = daysAwayFromDueDate;
            }
        }

        this.datesCreator.createLateDateCell(maximumLate);
        timeLine.fillDates();
    };

    this.fillDates = function() {
        var currentDate = new Date();
        this.datesCreator.createTodayDateCell(1, currentDate.toDateString());

        var numberOfNextDays = 6;
        for(index = 2; index < numberOfNextDays + 2; index++) {
            this.datesCreator.createDateCell(index, this.datesCreator.setNextDayAndGetDateString(currentDate));
        }
    };
};

var timeLine = new TimeLine();
timeLine.clear();

var projectKey = getParameterByName('project-key');

var pageTitle = projectKey + " - Event Organization Time Line";
document.title = pageTitle;
document.getElementById("time-line-name").innerHTML = pageTitle;

AJS.$(document).ready(
    function() {
        var rest = new RESTManager();
        rest.getIssues(projectKey, timeLine);
});