function TasksToggle() {
    "use strict";
    var that = this;
    that.scrollButtonIdEnd = '-scroll-button';

    that.createButton = function (listId) {
        var result;
        result = document.createElement('BUTTON');
        result.className = 'aui-icon aui-icon-small aui-iconfont-arrows-down';
        result.id = listId + that.scrollButtonIdEnd;
        result.style.margin = '0';
        result.style.width = '100%';
        return result;
    };

    that.addIfRequired = function (cell, listId) {
        if (that.containsScrollButton(cell) === true) {
            return false;
        }
        var list, scrollButton;
        list = jQuery('#' + listId);

        if (that.getHeightOfListElements(listId) > 200) {
            var scrollButton = that.createButton(listId);
            cell.appendChild(scrollButton);

            that.scrollOnClick(scrollButton, list);
            return true;
        }
        return false;
    };

    that.getHeightOfListElements = function (listId) {
        var result = 0;

        jQuery('#' + listId + ' li').each(function () {
            result += jQuery(this).height();
        });

        return result;
    };

    that.scrollOnClick = function (scrollButton, list) {
        var scrollDown = true;

        jQuery('#' + scrollButton.id).click(function () {
            scrollDown = that.shouldScrollDown(list, scrollDown);

            if (scrollDown === true) {
                that.scrollListWithOffset(list, 100);
            } else {
                that.scrollListWithOffset(list, -100);
            }

            that.changeButtonArrow(scrollButton, that.shouldScrollDown(list, scrollDown));
        });
    }

    that.containsScrollButton = function (cell) {
        return cell.lastChild.id.endsWith(that.scrollButtonIdEnd);
    }

    that.changeButtonArrow = function (button, scrollDown) {
        if (scrollDown === true) {
            button.className = 'aui-icon aui-icon-small aui-iconfont-arrows-down';
        } else {
            button.className = 'aui-icon aui-icon-small aui-iconfont-arrows-up';
        }
    };

    that.scrollListWithOffset = function (list, offset) {
        list.animate({scrollTop: list.scrollTop() + offset}, 1500);
    };

    that.reachedBottomOfList = function (list) {
        return Math.ceil(list.scrollTop() + list.innerHeight()) >= list[0].scrollHeight;
    };

    that.reachedTopOfList = function (list) {
        return list.scrollTop() === 0;
    };

    that.shouldScrollDown = function (list, previousState) {
        if (that.reachedBottomOfList(list) === true) {
            return false;
        }
        if (that.reachedTopOfList(list) === true) {
            return true;
        }
        return previousState;
    };
};