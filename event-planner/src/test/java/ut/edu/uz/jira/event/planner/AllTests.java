package ut.edu.uz.jira.event.planner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import ut.edu.uz.jira.event.planner.project.EventOrganizationProjectHookTest;
import ut.edu.uz.jira.event.planner.workflow.UpdateDueDatePostFunctionTest;

/**
 * Created by Dawid on 2015-11-05.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        MyComponentUnitTest.class,
        EventOrganizationProjectHookTest.class,
        UpdateDueDatePostFunctionTest.class
})
public class AllTests {
}
