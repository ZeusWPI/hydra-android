package be.ugent.zeus.hydra.utils;

import android.support.annotation.Nullable;

import org.threeten.bp.*;

/**
 * When we save to the database/parcel/..., we often save in epoch milli seconds.
 *
 * Then our time zone data is lost. That's why we first convert to a LocalDateTime using a fixed timezone.
 *
 * @author Niko Strijbol
 */
public class TtbUtils {

    private static final ZoneId zone = ZoneOffset.UTC.normalized();

    public static long serialize(@Nullable ZonedDateTime dateTime) {
        if(dateTime == null) {
            return -1;
        }
        return dateTime.withZoneSameInstant(zone).toInstant().toEpochMilli();
    }

    public static ZonedDateTime unserialize(long epochMilli) {
        if(epochMilli == -1) {
            return null;
        }
        return Instant.ofEpochMilli(epochMilli).atZone(zone);
    }

    public static long serialize(@Nullable LocalDateTime dateTime) {
        if(dateTime == null) {
            return -1;
        }
        return dateTime.atZone(zone).toInstant().toEpochMilli();
    }

    public static LocalDateTime unserializeLocal(long epochMilli) {
        if(epochMilli == -1) {
            return null;
        }
        return Instant.ofEpochMilli(epochMilli).atZone(zone).toLocalDateTime();
    }
}
