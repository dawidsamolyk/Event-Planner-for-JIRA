var plansList = new PlansList();
var plan = new Plan();
var domain = new Domain();
var component = new Component();
var task = new Task();
var subTask = new SubTask();

AJS.$(document).ready(
    function() {
        var rest = new RESTManager();
        rest.get(plan.id, plansList);
});

var subTaskListener = new ButtonListener(subTask);
    subTaskListener.onAddShowDialog();
    subTaskListener.onSaveDoPostResource();
    subTaskListener.onSaveHideDialog();
    subTaskListener.onSaveDoGetAndSaveInto(task);
    subTaskListener.onCancelCloseDialog();

var taskListener = new ButtonListener(task);
    taskListener.onShowDoGet([subTask]);
    taskListener.onAddShowDialog();
    taskListener.onSaveDoPostResource();
    taskListener.onSaveDoGetAndSaveInto(component);
    taskListener.onSaveHideDialog();
    taskListener.onCancelCloseDialog();

var componentListener = new ButtonListener(component);
    componentListener.onShowDoGet([task]);
    componentListener.onAddShowDialog();
    componentListener.onSaveDoPostResource();
    componentListener.onSaveDoGetAndSaveInto(plan);
    componentListener.onSaveHideDialog();
    componentListener.onCancelCloseDialog();

var domainListener = new ButtonListener(domain);
    domainListener.onAddShowDialog();
    domainListener.onSaveDoPostResource();
    domainListener.onSaveHideDialog();
    domainListener.onSaveDoGetAndSaveInto(plan);
    domainListener.onCancelCloseDialog();

var planListener = new ButtonListener(plan)
    planListener.onShowDoGet([domain, component]);
    planListener.onAddShowDialog();
    planListener.onSaveDoPostResource();
    planListener.onSaveHideDialog();
    planListener.onSaveDoGetAndSaveInto(plansList);
    planListener.onCancelCloseDialog();