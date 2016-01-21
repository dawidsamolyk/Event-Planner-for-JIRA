function DraggableListener() {
    "use strict";
    var that = this;

    that.gadgetCreator = new TaskGadgetCreator();
    that.restManager = new RESTManager();

    that.connect = function (cells, timeLine) {
        jQuery("ul.connectedSortable").sortable({
            connectWith: "ul",
            dropOnEmpty: true,
            stop: function (event, ui) {
                var taskGadget, targetColumnId, targetColumnDate;
                taskGadget = ui.item.context;

                targetColumnId = that.getParentId(taskGadget, 'TD');
                targetColumnDate = that.getKeyForValueWithId(cells, targetColumnId);

                that.changeTaskGadgetColor(taskGadget, targetColumnId, timeLine.shouldDisplayLateTasksColumn());
                that.changeTaskState(taskGadget.id, targetColumnId, targetColumnDate);

                timeLine.refreshLateDateCell();
            }
        }).disableSelection();
    };

    that.changeTaskGadgetColor = function (taskGadget, targetColumnId, displayingLateTasksColumn) {
        if (displayingLateTasksColumn === true && targetColumnId.endsWith('-0')) {
            that.gadgetCreator.changeToLate(taskGadget);

        } else if (targetColumnId.startsWith('todo')) {
            that.gadgetCreator.changeToToDo(taskGadget);

        } else if (targetColumnId.startsWith('done')) {
            that.gadgetCreator.changeToDone(taskGadget);
        }
    };

    that.changeTaskState = function (taskKey, targetColumnId, targetColumnDate) {
        that.restManager.postTask(taskKey, targetColumnId.substring(0, targetColumnId.indexOf('-')), targetColumnDate);
    };

    that.getKeyForValueWithId = function (cells, valueId) {
        var eachKey, eachValues;
        for (eachKey in cells) {
            eachValues = cells[eachKey];
            if (eachValues.toDo.id === valueId || eachValues.done.id === valueId) {
                return eachKey;
            }
        }
    };

    that.getParentId = function (element, type) {
        var node = element;
        while (node) {
            if (!node.nodeName) {
                break;
            }
            if (node.nodeName === type) {
                return node.id;
            }
            node = node.parentNode;
        }
    };
};