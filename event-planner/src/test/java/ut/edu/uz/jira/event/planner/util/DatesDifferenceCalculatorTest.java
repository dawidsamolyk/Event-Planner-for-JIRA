package ut.edu.uz.jira.event.planner.util;

import com.atlassian.jira.mock.component.MockComponentWorker;
import com.atlassian.jira.mock.security.MockAuthenticationContext;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.atlassian.jira.user.MockApplicationUser;
import edu.uz.jira.event.planner.exception.NullArgumentException;
import edu.uz.jira.event.planner.util.DatesDifference;
import edu.uz.jira.event.planner.util.DatesDifferenceCalculator;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Dawid on 09.02.2016.
 */
public class DatesDifferenceCalculatorTest {

    @Before
    public void setUp() {
        new MockComponentWorker()
                .addMock(JiraAuthenticationContext.class, new MockAuthenticationContext(new MockApplicationUser("test")))
                .init();
    }

    public DatesDifferenceCalculator getFixture() {
        return new DatesDifferenceCalculator();
    }

    @Test
    public void should_calculate_days_difference_in_the_same_week_and_month() throws ParseException, NullArgumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date current = sdf.parse("09/02/2016");
        Date newer = sdf.parse("12/02/2016");

        DatesDifference result = getFixture().calculate(current, newer);

        assertEquals(0, result.getMonths());
        assertEquals(3, result.getDays());
    }

    @Test
    public void should_calculate_months_and_days_in_other_months() throws ParseException, NullArgumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date current = sdf.parse("09/02/2016");
        Date newer = sdf.parse("12/03/2016");

        DatesDifference result = getFixture().calculate(current, newer);

        assertEquals(1, result.getMonths());
        assertEquals(3, result.getDays());
    }

    @Test
    public void should_calculate_one_year_as_12_months() throws ParseException, NullArgumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date current = sdf.parse("09/02/2016");
        Date newer = sdf.parse("09/02/2017");

        DatesDifference result = getFixture().calculate(current, newer);

        assertEquals(12, result.getMonths());
        assertEquals(0, result.getDays());
    }

    @Test
    public void should_calculate_months_in_other_year() throws ParseException, NullArgumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date current = sdf.parse("09/02/2016");
        Date newer = sdf.parse("09/03/2017");

        DatesDifference result = getFixture().calculate(current, newer);

        assertEquals(13, result.getMonths());
        assertEquals(0, result.getDays());
    }

    @Test
    public void should_calculate_months_and_days_in_other_year() throws ParseException, NullArgumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date current = sdf.parse("09/02/2016");
        Date newer = sdf.parse("10/03/2017");

        DatesDifference result = getFixture().calculate(current, newer);

        assertEquals(13, result.getMonths());
        assertEquals(1, result.getDays());
    }

    @Test
    public void should_calculate_days_and_months() throws ParseException, NullArgumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date current = sdf.parse("09/02/2016");
        Date newer = sdf.parse("30/03/2016");

        DatesDifference result = getFixture().calculate(current, newer);

        assertEquals(1, result.getMonths());
        assertEquals(20, result.getDays());
    }

    @Test
    public void should_calculate_days_difference_in_other_month() throws ParseException, NullArgumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date current = sdf.parse("28/02/2016");
        Date newer = sdf.parse("08/03/2016");

        DatesDifference result = getFixture().calculate(current, newer);

        assertEquals(0, result.getMonths());
        assertEquals(9, result.getDays());
    }

    @Test
    public void should_calculate_days_difference_in_other_year() throws ParseException, NullArgumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date current = sdf.parse("28/12/2015");
        Date newer = sdf.parse("02/01/2016");

        DatesDifference result = getFixture().calculate(current, newer);

        assertEquals(0, result.getMonths());
        assertEquals(5, result.getDays());
    }

    @Test
    public void should_calculate_months_and_days_difference_in_other_year() throws ParseException, NullArgumentException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date current = sdf.parse("28/10/2015");
        Date newer = sdf.parse("02/01/2016");

        DatesDifference result = getFixture().calculate(current, newer);

        assertEquals(2, result.getMonths());
        assertEquals(5, result.getDays());
    }
}
