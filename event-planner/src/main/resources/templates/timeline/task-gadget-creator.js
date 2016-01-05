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