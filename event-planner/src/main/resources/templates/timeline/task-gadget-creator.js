function TaskGadgetCreator() {
    "use strict";
    var that = this;

    that.create = function (issue) {
        var taskGadget, gadgetItem;

        taskGadget = that.createElement('LI', 'gadget color1 ui-state-default');
        taskGadget.style.position = 'relative';
        taskGadget.id = issue.key;

        gadgetItem = that.createElement('DIV', 'dashboard-item-frame gadget-container');
        taskGadget.appendChild(gadgetItem);

        gadgetItem.appendChild(that.createTitle(issue));
        gadgetItem.appendChild(that.createSummary(issue));

        return taskGadget;
    };

    that.createTitle = function (issue) {
        var title = that.createElement('DIV', 'dashboard-item-header');

        title.appendChild(that.createComponentIcon(issue));
        title.appendChild(that.createAvatar(issue.assigneeName, issue.avatarId));
        title.appendChild(that.createTitleText(issue));

        return title;
    };

    that.createTitleText = function (issue) {
        var title = that.createElement('SPAN', 'dashboard-item-title');

        title.title = issue.key + ' ' + issue.summary;
        title.style.alignment = 'left';
        title.style.verticalAlign = 'middle';
        title.style.padding = '4px 4px 4px 4px';
        title.appendChild(document.createTextNode(issue.key));

        return title;
    };

    that.createComponentIcon = function (issue) {
        var components = that.createElement('SPAN', 'aui-icon aui-icon-small aui-iconfont-component');

        components.style.filter = 'invert(100%)';
        components.style.WebkitFilter = 'invert(100%)';
        components.style.MozFilter = 'invert(100%)';
        components.style.OFilter = 'invert(100%)';
        components.style.MsFilter = 'invert(100%)';
        components.style.alignment = 'left';
        components.style.verticalAlign = 'middle';
        components.style.margin = '2px';

        var componentsNames = '';
        for (var loopIndex = 0; loopIndex < issue.componentsNames.length; loopIndex++) {
            componentsNames += issue.componentsNames[loopIndex];
        }
        components.title = componentsNames;

        return components;
    };

    that.createAvatar = function (assigneeName, avatarId) {
        var avatarImage, avatarLink;

        avatarLink = that.createElement('A');
        avatarLink.href = "/jira/secure/ViewProfile.jspa?name=" + assigneeName;
        avatarImage = that.createElement('IMG');
        avatarImage.src = "/jira/secure/useravatar?avatarId=" + avatarId + "&amp;s=16";
        avatarImage.title = assigneeName;
        avatarImage.height = 16;
        avatarImage.width = 16;
        avatarImage.style.margin = '2px';
        avatarImage.style.alignment = 'left';
        avatarImage.style.verticalAlign = 'middle';
        avatarImage.style.cursor = 'hand';

        avatarLink.appendChild(avatarImage);

        return avatarLink;
    };

    that.createSummary = function (issue) {
        var summaryElement = that.createElement('DIV', 'dashboard-item-content');

        summaryElement.style.margin = '1px';

        var summaryTextElement = that.createElement('A');
        summaryTextElement.href = AJS.contextPath() + "/browse/" + issue.key;
        summaryTextElement.style.fontSize = '13px';
        summaryTextElement.title = issue.summary;
        summaryTextElement.style.cursor = 'hand';
        summaryTextElement.appendChild(document.createTextNode(issue.summary));
        summaryElement.appendChild(summaryTextElement);

        return summaryElement;
    };

    that.createElement = function (elementName, className) {
        var result = document.createElement(elementName);
        if (className !== undefined) {
            result.className = className;
        }
        return result;
    };

    that.changeToDone = function (gadget) {
        gadget.className = 'gadget color7 ui-state-default';
        return gadget;
    };

    that.changeToToDo = function (gadget) {
        gadget.className = 'gadget color1 ui-state-default';
        return gadget;
    };

    that.changeToLate = function (gadget) {
        gadget.className = 'gadget ui-state-default';
        var headerElement = gadget.getElementsByClassName('dashboard-item-header')[0];
        headerElement.style.background = '#333333';
        return gadget;
    };

    that.createToDo = function (issue) {
        return that.changeToToDo(that.create(issue));
    };

    that.createDone = function (issue) {
        return that.changeToDone(that.create(issue));
    };

    that.createLate = function (issue) {
        return that.changeToLate(that.create(issue));
    };

    that.getRoot = function (element) {
        var result = element;
        console.log(result);
        while (result !== undefined && result.tagName !== 'LI') {
            result = result.parent;
        }
        return result;
    };
};