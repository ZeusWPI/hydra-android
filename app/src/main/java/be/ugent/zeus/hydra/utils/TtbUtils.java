package be.ugent.zeus.hydra.utils;

import android.support.annotation.Nullable;

import android.support.annotation.VisibleForTesting;
import org.threeten.bp.*;

/**
 * Utility methods to serialize {@link ZonedDateTime}, preserving the zone information.
 *
 * While implementation details are not guaranteed to stay the same, they might be important for use in database, they
 * are described here.
 *
 * Serializing happens by converting the given ZonedDateTime to another one, with a static, non-changing time zone. At
 * present this time zone is UTC. This new ZonedDateTime is then converted to an Instant, which is then converted to
 * epoch milliseconds.
 *
 * Unserializing happens by constructing a new ZonedDateTime from a given epoch milliseconds. As described in the method
 * documentation, we assume the time zone must be UTC. The returned ZonedDateTime is thus set to UTC and returned. This
 * result can then be used wherever it is needed. It doesn't matter that the result is not in the same timezone as the
 * original, as we converted the original in the {@link #serialize(ZonedDateTime)} function.
 *
 * @author Niko Strijbol
 */
public class TtbUtils {

    @VisibleForTesting
    static final ZoneOffset ZONE = ZoneOffset.UTC;

    /**
     * Serialize a ZonedDateTime to an epoch milli. The result of this function should be unserialized using
     * {@link #unserialize(long)}, as otherwise the time zone is not guaranteed to be correct.
     *
     * @param dateTime The ZonedDateTime to serialize.
     *
     * @return The representation in epoch milli.
     */
    public static long serialize(@Nullable ZonedDateTime dateTime) {
        if(dateTime == null) {
            return -1;
        }
        return dateTime.withZoneSameInstant(ZONE).toInstant().toEpochMilli();
    }

    /**
     * Unserialize a calculated epoch to a ZonedDateTime. The given epoch should be the result of the method above,
     * {@link #serialize(ZonedDateTime)}. If not, the behaviour is not specified.
     *
     * @param epochMilli The result of {@link #serialize(ZonedDateTime)}.
     *
     * @return A ZonedDateTime representing the epoch milli.
     */
    public static ZonedDateTime unserialize(long epochMilli) {
        if(epochMilli == -1) {
            return null;
        }
        return Instant.ofEpochMilli(epochMilli).atZone(ZONE);
    }
}