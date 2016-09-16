package be.ugent.zeus.hydra.models.sko;

import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.models.converters.ZonedThreeTenAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;

/**
 * An SKO artist.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings("unused")
public class Artist implements Serializable {

    @SerializedName("artist")
    private String name;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime start;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime end;
    private String picture;

    /**
     * @return The name of the act.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The start date, with time zone information.
     */
    public ZonedDateTime getStart() {
        return start;
    }

    /**
     * @return The end date, with time zone information.
     */
    public ZonedDateTime getEnd() {
        return end;
    }

    /**
     * Get the start date, converted to the local time zone. The resulting DateTime is the time as it is used
     * in the current time zone.
     *
     * This value is calculated every time, so if you need it a lot, cache it in a local variable.
     *
     * @return The converted start date.
     */
    public LocalDateTime getLocalStart() {
        return DateUtils.toLocalDateTime(getStart());
    }

    /**
     * Get the end date, converted to the local time zone. The resulting DateTime is the time as it is used
     * in the current time zone.
     *
     * This value is calculated every time, so if you need it a lot, cache it in a local variable.
     *
     * @return The converted end date.
     */
    public LocalDateTime getLocalEnd() {
        return DateUtils.toLocalDateTime(getEnd());
    }

    /**
     * @return The URL to the picture.
     */
    @Nullable
    public String getPicture() {
        return picture;
    }
}