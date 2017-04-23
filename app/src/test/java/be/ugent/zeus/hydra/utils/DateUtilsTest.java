package be.ugent.zeus.hydra.utils;

import org.junit.Test;
import org.threeten.bp.*;

import static be.ugent.zeus.hydra.utils.DateUtils.DATE_FORMATTER;
import static be.ugent.zeus.hydra.utils.DateUtils.DAY_FORMATTER;
import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class DateUtilsTest {

    @Test
    public void getFriendlyDate() {

        //Get some dates
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate overmorrow = today.plusDays(2);
        LocalDate yesterday = today.minusDays(1);
        LocalDate thisWeek = today.plusDays(6);
        LocalDate nextWeek = today.plusDays(8);
        LocalDate far = today.plusDays(50);
        LocalDate exact = today.plusMonths(1);

        //Assert correct results
        assertEquals("vandaag", DateUtils.getFriendlyDate(today));
        assertEquals("morgen", DateUtils.getFriendlyDate(tomorrow));
        assertEquals("overmorgen", DateUtils.getFriendlyDate(overmorrow));
        assertEquals(DATE_FORMATTER.format(yesterday), DateUtils.getFriendlyDate(yesterday));
        assertEquals(DAY_FORMATTER.format(thisWeek).toLowerCase(), DateUtils.getFriendlyDate(thisWeek));
        assertEquals(DATE_FORMATTER.format(far), DateUtils.getFriendlyDate(far));
        assertEquals(DATE_FORMATTER.format(exact), DateUtils.getFriendlyDate(exact));
    }

    @Test
    public void toLocalDateTime() {
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(1996, 4, 18, 8, 0), ZoneOffset.UTC);
        LocalDateTime converted = DateUtils.toLocalDateTime(dateTime);
        assertEquals(dateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime(), converted);
    }
}