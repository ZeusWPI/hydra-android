package be.ugent.zeus.hydra.common.converter;

import org.junit.Test;
import org.threeten.bp.Instant;
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

    @Test
    public void fromInstant() {
        Instant now = Instant.now();
        assertEquals(now.toString(), DateTypeConverters.fromInstant(now));
    }

    @Test
    public void fromInstantNull() {
        assertEquals(null, DateTypeConverters.fromInstant(null));
    }

    @Test
    public void toInstant() {
        Instant now = Instant.now();
        assertEquals(now, DateTypeConverters.toInstant(now.toString()));
    }

    @Test
    public void toInstantNull() {
        assertEquals(null, DateTypeConverters.toInstant(null));
    }
}