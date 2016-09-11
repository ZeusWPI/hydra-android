package be.ugent.zeus.hydra.models.sko;

import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.models.converters.DateTimeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * An SKO artist.
 *
 * @author Niko Strijbol
 */
public class Artist implements Serializable {

    @SerializedName("artist")
    private String name;
    @JsonAdapter(DateTimeAdapter.class)
    private DateTime start;
    @JsonAdapter(DateTimeAdapter.class)
    private DateTime end;
    private String picture;

    public String getName() {
        return name;
    }

    public DateTime getStart() {
        return start;
    }

    public DateTime getEnd() {
        return end;
    }

    @Nullable
    public String getPicture() {
        return picture;
    }
}