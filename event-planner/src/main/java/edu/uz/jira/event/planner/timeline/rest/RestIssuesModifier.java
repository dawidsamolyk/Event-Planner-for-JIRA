package edu.uz.jira.event.planner.timeline.rest;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.UpdateIssueRequest;
import com.atlassian.jira.issue.status.category.StatusCategory;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;
import edu.uz.jira.event.planner.project.plan.rest.RestManagerHelper;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * REST manager which modifies Issues.
 */
@Path("/issue/modify")
public class RestIssuesModifier {
    private final RestManagerHelper helper;
    private final IssueManager issueManager;
    private JiraAuthenticationContext jiraAuthenticationContext;
    private IssueService issueService;

    /**
     * Constructor.
     */
    public RestIssuesModifier() {
        helper = new RestManagerHelper();
        issueManager = ComponentAccessor.getIssueManager();
        jiraAuthenticationContext = ComponentAccessor.getJiraAuthenticationContext();
        issueService = ComponentAccessor.getIssueService();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(final IssueData issueData, @Context final HttpServletRequest request) {
        MutableIssue issue = issueManager.getIssueByKeyIgnoreCase(issueData.getKey());

        if (issue == null) {
            return helper.buildStatus(Response.Status.NOT_FOUND);
        }
        setDueDate(issue, issueData);
        setState(issue, issueData.getState());

        issueManager.updateIssue(getLoggedInUser(), issue, UpdateIssueRequest.builder().build());

        if (isIssueModified(issueData)) {
            return helper.buildStatus(Response.Status.OK);
        }
        return helper.buildStatus(Response.Status.NOT_MODIFIED);
    }

    private boolean isIssueModified(final IssueData issueData) {
        MutableIssue issue = issueManager.getIssueByKeyIgnoreCase(issueData.getKey());

        boolean validDueDate = issue.getDueDate().getTime() == issueData.getDueDateTime();

        StatusCategory statusCategory = issue.getStatusObject().getStatusCategory();
        boolean validState;
        if (issueData.getState().equals("done")) {
            validState = statusCategory.equals(StatusCategory.COMPLETE);
        } else {
            validState = statusCategory.equals(StatusCategory.TO_DO) || statusCategory.equals(StatusCategory.IN_PROGRESS);
        }

        return validDueDate && validState;
    }

    private MutableIssue setState(final MutableIssue issue, final String state) {
        JiraWorkflow workflow = ComponentAccessor.getWorkflowManager().getWorkflow(issue);
        if (workflow == null) {
            return issue;
        }
        StepDescriptor step = workflow.getLinkedStep(issue.getStatusObject());

        ActionDescriptor actionToPerform = null;
        for (ActionDescriptor eachAction : (List<ActionDescriptor>) step.getActions()) {
            String name = eachAction.getName();

            if (state.equals("done") && (name.equals("Done") || name.equals("Resolved"))) {
                actionToPerform = eachAction;
            } else if (state.equals("todo") && name.equals("Reopen")) {
                actionToPerform = eachAction;
            }
        }

        if (actionToPerform == null) {
            return issue;
        }

        IssueService.TransitionValidationResult result = issueService.validateTransition(getLoggedInUser(), issue.getId(), actionToPerform.getId(), issueService.newIssueInputParameters());
        if (result.isValid()) {
            IssueService.IssueResult transitionResult = issueService.transition(getLoggedInUser(), result);
            return transitionResult.getIssue();
        }
        return issue;
    }

    private ApplicationUser getLoggedInUser() {
        return jiraAuthenticationContext.getUser();
    }

    private void setDueDate(final MutableIssue issue, final IssueData issueData) {
        if (issueData.isLate()) {
            Calendar calendar = Calendar.getInstance(jiraAuthenticationContext.getLocale());
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -1);
            issue.setDueDate(new Timestamp(calendar.getTimeInMillis()));
        } else {
            issue.setDueDate(new Timestamp(issueData.getDueDateTime()));
        }
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class IssueData {
        @XmlElement
        private String key;
        @XmlElement
        private String state;
        @XmlElement
        private Long dueDateTime;

        public IssueData() {
            this("", "", null);
        }

        public IssueData(final String key, final String state, final Long dueDateTime) {
            this.key = key;
            this.state = state;
            this.dueDateTime = dueDateTime;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public Long getDueDateTime() {
            return dueDateTime;
        }

        public void setDueDateTime(Long dueDateTime) {
            this.dueDateTime = dueDateTime;
        }

        public boolean isLate() {
            return dueDateTime == null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            IssueData issueData = (IssueData) o;

            if (key != null ? !key.equals(issueData.key) : issueData.key != null) return false;
            if (state != null ? !state.equals(issueData.state) : issueData.state != null) return false;
            return dueDateTime != null ? dueDateTime.equals(issueData.dueDateTime) : issueData.dueDateTime == null;

        }

        @Override
        public int hashCode() {
            int result = key != null ? key.hashCode() : 0;
            result = 31 * result + (state != null ? state.hashCode() : 0);
            result = 31 * result + (dueDateTime != null ? dueDateTime.hashCode() : 0);
            return result;
        }
    }
}
