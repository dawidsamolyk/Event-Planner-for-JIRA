function TimeLineTasksCreator(tasksToDoId, doneTasksId) {
    this.tasksToDoId = tasksToDoId;
    this.doneTasksId = doneTasksId;
    this.createdTasksCells = {};
    this.createdDoneTasksCells = {};
    this.getElementById = function(id) { return document.getElementById(id); };

    this.createTaskCell = function(index) {
        var tasksToDo = this.getElementById(this.tasksToDoId);
        var result = tasksToDo.insertCell(index);
        result.style.verticalAlign = 'bottom';
        result.style.padding = 0;

        this.createdTasksCells[index] = result;
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

        this.createdDoneTasksCells[index] = result;
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

    this.setAsDeadline = function(cell) {
        result.style.borderLeft = '3px solid #14892c';
        result.style.borderRight = '3px solid #14892c';
        result.style.background = '#14892c';
    };
};