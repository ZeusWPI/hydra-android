package be.ugent.zeus.hydra.utils;

import android.content.Context;

import be.ugent.zeus.hydra.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.threeten.bp.*;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import static be.ugent.zeus.hydra.utils.DateUtils.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class DateUtilsTest {

    @Test
    public void testFriendlyDate() {

        //Get some dates
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate overmorrow = today.plusDays(2);
        LocalDate yesterday = today.minusDays(1);
        LocalDate thisWeek = today.plusDays(6);
        LocalDate far = today.plusDays(50);
        LocalDate exact = today.plusMonths(1);

        DateTimeFormatter formatter = getDateFormatterForStyle(FormatStyle.MEDIUM);

        Context c = RuntimeEnvironment.application;
        String todayResult = c.getString(R.string.date_today);
        String tomorrowResult = c.getString(R.string.date_tomorrow);
        String overmorrowResult = c.getString(R.string.date_overmorrow);

        //Assert correct results
        assertEquals(todayResult, DateUtils.getFriendlyDate(c, today));
        assertEquals(tomorrowResult, DateUtils.getFriendlyDate(c, tomorrow));
        assertEquals(overmorrowResult, DateUtils.getFriendlyDate(c, overmorrow));
        assertEquals(formatter.format(yesterday), DateUtils.getFriendlyDate(c, yesterday));
        assertEquals(DAY_FORMATTER.format(thisWeek).toLowerCase(), DateUtils.getFriendlyDate(c, thisWeek));
        assertEquals(formatter.format(far), DateUtils.getFriendlyDate(c, far));
        assertEquals(formatter.format(exact), DateUtils.getFriendlyDate(c, exact));
    }

    @Test
    public void testLongFriendlyDate() {

        //Get some dates
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate overmorrow = today.plusDays(2);
        LocalDate yesterday = today.minusDays(1);
        LocalDate thisWeek = today.plusDays(6);
        LocalDate far = today.plusDays(50);
        LocalDate exact = today.plusMonths(1);

        DateTimeFormatter formatter = getDateFormatterForStyle(FormatStyle.LONG);

        Context c = RuntimeEnvironment.application;
        String todayResult = c.getString(R.string.date_today);
        String tomorrowResult = c.getString(R.string.date_tomorrow);
        String overmorrowResult = c.getString(R.string.date_overmorrow);

        //Assert correct results
        assertEquals(todayResult, DateUtils.getFriendlyDate(c, today, FormatStyle.LONG));
        assertEquals(tomorrowResult, DateUtils.getFriendlyDate(c, tomorrow, FormatStyle.LONG));
        assertEquals(overmorrowResult, DateUtils.getFriendlyDate(c, overmorrow, FormatStyle.LONG));
        assertEquals(formatter.format(yesterday), DateUtils.getFriendlyDate(c, yesterday, FormatStyle.LONG));
        assertEquals(DAY_FORMATTER.format(thisWeek).toLowerCase(), DateUtils.getFriendlyDate(c, thisWeek, FormatStyle.LONG));
        assertEquals(formatter.format(far), DateUtils.getFriendlyDate(c, far, FormatStyle.LONG));
        assertEquals(formatter.format(exact), DateUtils.getFriendlyDate(c, exact, FormatStyle.LONG));
    }
}