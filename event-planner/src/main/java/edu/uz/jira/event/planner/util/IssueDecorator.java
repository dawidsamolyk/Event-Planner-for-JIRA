package edu.uz.jira.event.planner.util;

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
    private boolean done;
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
        if(sourceDueDate != null) {
            dueDate = sourceDueDate.getTime();
            daysAwayFromDueDate = DUE_DATE_INDICATOR.getDaysAwayFromDueDate(sourceDueDate);
        }

        setComponents(source);
        setDone(source);

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
            Iterator<ProjectComponent> componentsIterator = components.iterator();
            componentsNames = new String[components.size()];
            for (int index = 0; index < components.size(); index++) {
                componentsNames[index] = componentsIterator.next().getName();
            }
        }
    }

    public boolean isLate() {
        return daysAwayFromDueDate < 0;
    }

    public boolean isDone() {
        return done;
    }

    private void setDone(@Nonnull final Issue source) {
        Status statusObject = source.getStatusObject();
        StatusCategory statusCategory = statusObject.getStatusCategory();
        done = statusCategory.getKey().equals(StatusCategory.COMPLETE);
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
            Long defaultUserAvatarId = ComponentAccessor.getAvatarManager().getDefaultAvatarId(Avatar.Type.USER);
            avatarId = defaultUserAvatarId;
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

        if (isDone() != that.isDone()) return false;
        if (getAvatarId() != that.getAvatarId()) return false;
        if (getDaysAwayFromDueDate() != that.getDaysAwayFromDueDate()) return false;
        if (getKey() != null ? !getKey().equals(that.getKey()) : that.getKey() != null) return false;
        if (getSummary() != null ? !getSummary().equals(that.getSummary()) : that.getSummary() != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(getComponentsNames(), that.getComponentsNames())) return false;
        if (getAssigneeName() != null ? !getAssigneeName().equals(that.getAssigneeName()) : that.getAssigneeName() != null)
            return false;
        return !(getDueDate() != null ? !getDueDate().equals(that.getDueDate()) : that.getDueDate() != null);
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }
}
