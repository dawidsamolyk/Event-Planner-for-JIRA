function DraggableListener() {
    "use strict";
    var that = this;

    that.gadgetCreator = new TaskGadgetCreator();
    that.restManager = new RESTManager();

    that.connect = function (cells, displayingLateTasksColumn) {
        jQuery("ul.connectedSortable").sortable({
            connectWith: "ul",
            dropOnEmpty: true,
            start: function (event, ui) {
                ui.item.startPos = ui.item.index();
            },
            stop: function (event, ui) {
                var taskGadget, targetColumnId, targetColumnDate;
                taskGadget = ui.item.context;

                if (ui.item.startPos === ui.item.index()) {
                    return;
                }

                targetColumnId = that.getParentId(taskGadget, 'TD');
                targetColumnDate = that.getKeyForValueWithId(cells, targetColumnId);

                that.changeTaskGadgetColor(taskGadget, targetColumnId, displayingLateTasksColumn);
                that.changeTaskState(taskGadget.id, targetColumnId, targetColumnDate);
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
        that.restManager.postTask(taskKey, targetColumnId, targetColumnDate);
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