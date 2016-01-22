package edu.uz.jira.event.planner.timeline.rest;

import com.atlassian.crowd.embedded.api.User;
import com.atlassian.jira.avatar.Avatar;
import com.atlassian.jira.bc.project.component.ProjectComponent;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.issue.status.category.StatusCategory;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.user.ApplicationUsers;
import edu.uz.jira.event.planner.project.issue.DueDateIndicator;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * Decorator of Issue which provides most wanted informations for Event Organization Timeline view about each Issue.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class IssueDecorator {
    private final static DueDateIndicator DUE_DATE_INDICATOR = new DueDateIndicator();
    @XmlElement
    private final String key;
    @XmlElement
    private String status;
    @XmlElement
    private String summary;
    @XmlElement
    private String[] componentsNames;
    @XmlElement
    private long avatarId;
    @XmlElement
    private int daysAwayFromDueDate;
    @XmlElement
    private String assigneeName;
    @XmlElement
    private Long dueDate;

    /**
     * Constructor.
     *
     * @param source Source of data.
     */
    public IssueDecorator(@Nonnull final Issue source) {
        key = source.getKey();
        summary = source.getSummary();

        Timestamp sourceDueDate = source.getDueDate();
        if (sourceDueDate != null) {
            dueDate = sourceDueDate.getTime();
            daysAwayFromDueDate = DUE_DATE_INDICATOR.getDaysAwayFromDueDate(sourceDueDate);
        }

        setComponents(source);
        setStatus(source);

        User assignee = source.getAssignee();
        if (assignee != null) {
            assigneeName = assignee.getName();
        }
        setAvatarId(assignee);
    }

    public Long getDueDate() {
        return dueDate;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public String getKey() {
        return key;
    }

    private void setComponents(@Nonnull final Issue source) {
        Collection<ProjectComponent> components = source.getComponentObjects();

        if (components != null && !components.isEmpty()) {
            componentsNames = new String[components.size()];

            Iterator<ProjectComponent> componentsIterator = components.iterator();
            for (int index = 0; index < components.size(); index++) {
                componentsNames[index] = componentsIterator.next().getName();
            }
        } else {
            componentsNames = new String[]{};
        }
    }

    public boolean isLate() {
        return daysAwayFromDueDate < 0;
    }

    public boolean isDone() {
        return status.equals(StatusCategory.COMPLETE);
    }

    private void setStatus(@Nonnull final Issue source) {
        Status statusObject = source.getStatusObject();
        StatusCategory statusCategory = statusObject.getStatusCategory();
        status = statusCategory.getKey();
    }

    public String getSummary() {
        return summary;
    }

    public String[] getComponentsNames() {
        return componentsNames;
    }

    public long getAvatarId() {
        return avatarId;
    }

    private void setAvatarId(final User assignee) {
        if (assignee != null) {
            ApplicationUser remoteUser = ComponentAccessor.getJiraAuthenticationContext().getUser();
            ApplicationUser assigneeUser = ApplicationUsers.from(assignee);

            Avatar assigneeAvatar = ComponentAccessor.getAvatarService().getAvatar(remoteUser, assigneeUser);
            if (assigneeAvatar != null) {
                avatarId = assigneeAvatar.getId();
            }
        }
        if (assignee == null || avatarId == 0) {
            avatarId = ComponentAccessor.getAvatarManager().getDefaultAvatarId(Avatar.Type.USER);
        }
    }

    public int getDaysAwayFromDueDate() {
        return daysAwayFromDueDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IssueDecorator that = (IssueDecorator) o;

        if (avatarId != that.avatarId) return false;
        if (daysAwayFromDueDate != that.daysAwayFromDueDate) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (summary != null ? !summary.equals(that.summary) : that.summary != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(componentsNames, that.componentsNames)) return false;
        if (assigneeName != null ? !assigneeName.equals(that.assigneeName) : that.assigneeName != null) return false;
        return dueDate != null ? dueDate.equals(that.dueDate) : that.dueDate == null;
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}
