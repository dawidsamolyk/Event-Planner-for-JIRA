package it.edu.uz.jira.event.planner.project;

import com.atlassian.jira.functest.framework.FuncTestCase;
import com.atlassian.jira.testkit.client.Backdoor;
import com.atlassian.jira.testkit.client.util.TestKitLocalEnvironmentData;
import com.atlassian.jira.testkit.client.util.TimeBombLicence;

public class EventOrganizationProjectHookTest extends FuncTestCase {

    @Override
    protected void setUpTest() {
        super.setUpTest();
        Backdoor testKit = new Backdoor(new TestKitLocalEnvironmentData());
        testKit.restoreBlankInstance(TimeBombLicence.LICENCE_FOR_TESTING);
        testKit.usersAndGroups().addUser("test-user");
        testKit.websudo().disable();
        testKit.subtask().enable();
    }

    public void testProjectCreation() {

    }
}
