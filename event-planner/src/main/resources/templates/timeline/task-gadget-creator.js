function TaskGadgetCreator() {
    var that = this;

    that.create = function(gadgetClass, issue) {
        var issueKey = issue.key;

        var taskGadget = that.createElement('DIV', gadgetClass);
        taskGadget.style.position = 'relative';
        taskGadget.id = issueKey;

        var gadgetItem = that.createElement('DIV', 'dashboard-item-frame gadget-container');
        taskGadget.appendChild(gadgetItem);

        var titleElement = that.createElement('DIV', 'dashboard-item-header');
        gadgetItem.appendChild(titleElement);

        var titleTextElement = that.createElement('H3', 'dashboard-item-title');
        var componentsNames = issue.componentsNames;
        if(componentsNames.length > 0 && componentsNames[0] && componentsNames[0].length > 0) {
            titleTextElement.appendChild(document.createTextNode(componentsNames[0]));
        }
        titleElement.appendChild(titleTextElement);

        var summaryElement = that.createElement('DIV', 'dashboard-item-content');
        gadgetItem.appendChild(summaryElement);

        var avatarLink = that.createElement('A');
        avatarLink.href = "/jira/secure/ViewProfile.jspa?name=" + issue.assigneeName;

        var avatarImage = that.createElement('IMG');
        avatarImage.src = "/jira/secure/useravatar?avatarId=" + issue.avatarId + "&amp;s=32";
        avatarImage.height = 32;
        avatarImage.width = 32;
        avatarLink.appendChild(avatarImage);
        summaryElement.appendChild(avatarLink);

        var summaryTextElement = that.createElement('A');
        summaryTextElement.href = AJS.contextPath() + "/browse/" + issueKey;
        summaryTextElement.appendChild(document.createTextNode(issue.summary));
        summaryElement.appendChild(summaryTextElement);

        return taskGadget;
    };

    that.createElement = function(elementName, className) {
        var result = document.createElement(elementName);
        result.className = className;
        return result;
    };

    that.createToDo = function(issue) {
         return that.create('gadget color1', issue);
    };

    that.createDone = function(issue) {
        return that.create('gadget color7', issue);
    };

    that.createLate = function(issue){
        var result = that.create('gadget', issue);
        var headerElement = result.getElementsByClassName('dashboard-item-header')[0];
        headerElement.style.background = '#333333';
        return result;
    };
};