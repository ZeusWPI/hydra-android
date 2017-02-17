package be.ugent.zeus.hydra.models.sko;

import android.os.Parcel;
import android.os.Parcelable;

import be.ugent.zeus.hydra.models.converters.ZonedThreeTenAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import com.google.gson.annotations.JsonAdapter;
import java8.util.Objects;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;

/**
 * An SKO artist.
 *
 * An artist is uniquely defined by his/her name, stage, start and stop time.
 *
 * @author Niko Strijbol
 */
public final class Artist implements Serializable, Parcelable {

    private String name;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime start;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime end;
    private String banner;
    private String image;
    private String content;
    private String stage;

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

    public String getBanner() {
        return banner;
    }

    public String getImage() {
        return image;
    }

    public String getContent() {
        return content;
    }

    public String getStage() {
        return stage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeSerializable(this.start);
        dest.writeSerializable(this.end);
        dest.writeString(this.banner);
        dest.writeString(this.image);
        dest.writeString(this.content);
        dest.writeString(this.stage);
    }

    public Artist() {
    }

    private Artist(Parcel in) {
        this.name = in.readString();
        this.start = (ZonedDateTime) in.readSerializable();
        this.end = (ZonedDateTime) in.readSerializable();
        this.banner = in.readString();
        this.image = in.readString();
        this.content = in.readString();
        this.stage = in.readString();
    }

    public static final Parcelable.Creator<Artist> CREATOR = new Parcelable.Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return Objects.equals(name, artist.name) &&
                Objects.equals(start, artist.start) &&
                Objects.equals(end, artist.end) &&
                Objects.equals(stage, artist.stage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, start, end, stage);
    }
}