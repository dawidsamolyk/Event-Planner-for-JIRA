package ut.edu.uz.jira.event.planner.project;

import com.atlassian.jira.project.version.Version;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Mockito.mock;

public class MocksProvider {
    public static Collection<Version> getMockVersionsNamed(String... versionsNames) {
        Collection<Version> versions = new ArrayList<Version>();

        for(String eachName: versionsNames) {
            Version mockVersion = mock(Version.class);
            Mockito.when(mockVersion.getName()).thenReturn(eachName);
            versions.add(mockVersion);
        }


        return versions;
    }
}
