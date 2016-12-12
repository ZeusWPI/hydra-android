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
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate overmorrow = LocalDate.now().plusDays(2);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate thisWeek = LocalDate.now().plusDays(6);
        LocalDate nextWeek = LocalDate.now().plusDays(8);
        LocalDate far = LocalDate.now().plusDays(50);

        //Assert correct results
        assertEquals(DateUtils.getFriendlyDate(today), "vandaag");
        assertEquals(DateUtils.getFriendlyDate(tomorrow), "morgen");
        assertEquals(DateUtils.getFriendlyDate(overmorrow), "overmorgen");
        assertEquals(DateUtils.getFriendlyDate(yesterday), DATE_FORMATTER.format(yesterday));
        assertEquals(DateUtils.getFriendlyDate(thisWeek), DAY_FORMATTER.format(thisWeek).toLowerCase());
        assertEquals(DateUtils.getFriendlyDate(nextWeek), "volgende " + DAY_FORMATTER.format(nextWeek).toLowerCase());
        assertEquals(DateUtils.getFriendlyDate(far), DATE_FORMATTER.format(far));
    }

    @Test
    public void toLocalDateTime() {
        ZonedDateTime dateTime = ZonedDateTime.of(LocalDateTime.of(1996, 4, 18, 8, 0), ZoneOffset.UTC);
        LocalDateTime converted = DateUtils.toLocalDateTime(dateTime);

        assertEquals(converted, dateTime.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime());
    }
}