package ut.edu.uz.jira.event.planner.util;

import edu.uz.jira.event.planner.project.plan.model.NamedEntityWithDescription;
import edu.uz.jira.event.planner.util.text.EntityNameExtractor;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.mock;

public class EntityNameExtractorTest {

    @Test
    public void should_Return_Names_Of_Entities() {
        EntityNameExtractor fixture = new EntityNameExtractor();
        String[] expectedResult = new String[]{"Test name 1", "Test name2"};
        NamedEntityWithDescription[] mockEntities = new NamedEntityWithDescription[expectedResult.length];
        for (int index = 0; index < mockEntities.length; index++) {
            NamedEntityWithDescription mock = mock(NamedEntityWithDescription.class);
            Mockito.when(mock.getName()).thenReturn(expectedResult[index]);
            mockEntities[index] = mock;
        }

        String[] result = fixture.getNames(mockEntities);

        assertArrayEquals(expectedResult, result);
    }

    @Test
    public void should_Return_Empty_Array_If_Input_Is_Null() {
        EntityNameExtractor fixture = new EntityNameExtractor();

        String[] result = fixture.getNames(null);

        assertEquals(0, result.length);
    }

    @Test
    public void should_Return_Empty_Array_If_Input_Is_Empty() {
        EntityNameExtractor fixture = new EntityNameExtractor();

        String[] result = fixture.getNames(new NamedEntityWithDescription[]{});

        assertEquals(0, result.length);
    }

    @Test
    public void should_Return_Array_Without_Null_Values() {
        EntityNameExtractor fixture = new EntityNameExtractor();
        String[] expectedResult = new String[]{"Test name 1", null, "test name 2"};
        NamedEntityWithDescription[] mockEntities = new NamedEntityWithDescription[expectedResult.length];
        for (int index = 0; index < mockEntities.length; index++) {
            NamedEntityWithDescription mock = mock(NamedEntityWithDescription.class);
            Mockito.when(mock.getName()).thenReturn(expectedResult[index]);
            mockEntities[index] = mock;
        }

        String[] result = fixture.getNames(mockEntities);

        assertArrayEquals(new String[]{"Test name 1", "test name 2"}, result);
    }
}
