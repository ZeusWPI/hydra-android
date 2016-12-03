package be.ugent.zeus.hydra.utils;

import org.junit.Before;
import org.junit.Test;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Niko Strijbol
 */
public class TtbUtilsTest {

    private ZonedDateTime zonedDateTime;
    private long zoneMilli;

    @Before
    public void setUp() {
        LocalDateTime localDateTime = LocalDateTime.now();
        zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
        zoneMilli = zonedDateTime.withZoneSameInstant(TtbUtils.ZONE).toInstant().toEpochMilli();
    }

    @Test
    public void unserializeZoned() {
        //Compare the unserialized result.
        assertEquals(zonedDateTime, TtbUtils.unserialize(zoneMilli).withZoneSameInstant(zonedDateTime.getZone()));
    }

    @Test
    public void unserializeZonedNull() {
        assertNull(TtbUtils.unserialize(-1));
    }

    @Test
    public void serializeZoned() {
        assertEquals(zoneMilli, TtbUtils.serialize(zonedDateTime));
    }

    @Test
    public void serializeZonedNull() {
        assertEquals(-1, TtbUtils.serialize(null));
    }
}