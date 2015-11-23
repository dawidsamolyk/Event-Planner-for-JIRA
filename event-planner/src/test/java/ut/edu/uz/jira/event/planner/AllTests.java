package ut.edu.uz.jira.event.planner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ut.edu.uz.jira.event.planner.project.EventOrganizationProjectHookTest;
import ut.edu.uz.jira.event.planner.project.issue.DueDateIndicatorTest;
import ut.edu.uz.jira.event.planner.servlet.EventOrganizationConfigServletTest;
import ut.edu.uz.jira.event.planner.workflow.UpdateDueDatePostFunctionTest;

/**
 * Created by Dawid on 2015-11-05.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventOrganizationProjectHookTest.class,
        UpdateDueDatePostFunctionTest.class,
        DueDateIndicatorTest.class,
        EventOrganizationConfigServletTest.class
})
public class AllTests {
}
