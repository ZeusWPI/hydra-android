package be.ugent.zeus.hydra.data.database;

import org.junit.Test;
import org.threeten.bp.OffsetDateTime;

import static org.junit.Assert.assertEquals;

/**
 * @author Niko Strijbol
 */
public class DateTypeConvertersTest {

    @Test
    public void toOffsetDateTime() {
        OffsetDateTime now = OffsetDateTime.now();
        String expected = now.format(DateTypeConverters.OFFSET_FORMATTER);
        assertEquals(expected, DateTypeConverters.fromOffsetDateTime(now));
    }

    @Test
    public void toOffsetDateTimeNull() {
        assertEquals(null, DateTypeConverters.fromOffsetDateTime(null));
    }

    @Test
    public void fromOffsetDateTime() {
        OffsetDateTime now = OffsetDateTime.now();
        String value = now.format(DateTypeConverters.OFFSET_FORMATTER);
        assertEquals(now, DateTypeConverters.toOffsetDateTime(value));
    }

    @Test
    public void fromOffsetDateTimeNull() {
        assertEquals(null, DateTypeConverters.toOffsetDateTime(null));
    }
}