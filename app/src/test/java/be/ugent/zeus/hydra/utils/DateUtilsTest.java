package be.ugent.zeus.hydra.utils;

import org.junit.Test;
import org.threeten.bp.*;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;

import static be.ugent.zeus.hydra.utils.DateUtils.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class DateUtilsTest {

    @Test
    public void testFriendlyDate() {

        //Get some dates
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate overmorrow = today.plusDays(2);
        LocalDate yesterday = today.minusDays(1);
        LocalDate thisWeek = today.plusDays(6);
        LocalDate nextWeek = today.plusDays(8);
        LocalDate far = today.plusDays(50);
        LocalDate exact = today.plusMonths(1);

        DateTimeFormatter formatter = getDateFormatterForStyle(FormatStyle.MEDIUM);

        //Assert correct results
        assertEquals("vandaag", DateUtils.getFriendlyDate(today));
        assertEquals("morgen", DateUtils.getFriendlyDate(tomorrow));
        assertEquals("overmorgen", DateUtils.getFriendlyDate(overmorrow));
        assertEquals(formatter.format(yesterday), DateUtils.getFriendlyDate(yesterday));
        assertEquals(DAY_FORMATTER.format(thisWeek).toLowerCase(), DateUtils.getFriendlyDate(thisWeek));
        assertEquals(formatter.format(far), DateUtils.getFriendlyDate(far));
        assertEquals(formatter.format(exact), DateUtils.getFriendlyDate(exact));
    }

    @Test
    public void testLongFriendlyDate() {

        //Get some dates
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate overmorrow = today.plusDays(2);
        LocalDate yesterday = today.minusDays(1);
        LocalDate thisWeek = today.plusDays(6);
        LocalDate nextWeek = today.plusDays(8);
        LocalDate far = today.plusDays(50);
        LocalDate exact = today.plusMonths(1);

        DateTimeFormatter formatter = getDateFormatterForStyle(FormatStyle.LONG);

        //Assert correct results
        assertEquals("vandaag", DateUtils.getFriendlyDate(today, FormatStyle.LONG));
        assertEquals("morgen", DateUtils.getFriendlyDate(tomorrow, FormatStyle.LONG));
        assertEquals("overmorgen", DateUtils.getFriendlyDate(overmorrow, FormatStyle.LONG));
        assertEquals(formatter.format(yesterday), DateUtils.getFriendlyDate(yesterday, FormatStyle.LONG));
        assertEquals(DAY_FORMATTER.format(thisWeek).toLowerCase(), DateUtils.getFriendlyDate(thisWeek, FormatStyle.LONG));
        assertEquals(formatter.format(far), DateUtils.getFriendlyDate(far, FormatStyle.LONG));
        assertEquals(formatter.format(exact), DateUtils.getFriendlyDate(exact, FormatStyle.LONG));
    }

    @Test
    public void toLocalDateTime() {
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(1996, 4, 18, 8, 0), ZoneOffset.UTC);
        LocalDateTime converted = DateUtils.toLocalDateTime(dateTime);
        assertEquals(dateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(), converted);
    }
}