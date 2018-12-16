package be.ugent.zeus.hydra.sko.lineup;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.utils.DateUtils;
import java9.util.Objects;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

/**
 * An SKO artist.
 *
 * An artist is uniquely defined by his/her name, stage, start and stop time.
 *
 * @author Niko Strijbol
 */
public final class Artist implements Parcelable {

    private static final String LOCATION = "Sint-Pietersplein, Gent";

    private String name;
    private OffsetDateTime start;
    private OffsetDateTime end;
    private String banner;
    private String image;
    private String content;
    private String stage;
    private String link;

    /**
     * @return The name of the act.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The start date, with time zone information.
     */
    public OffsetDateTime getStart() {
        return start;
    }

    /**
     * @return The end date, with time zone information.
     */
    public OffsetDateTime getEnd() {
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
        return getStart() == null ? null : DateUtils.toLocalDateTime(getStart());
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
        return getEnd() == null ? null : DateUtils.toLocalDateTime(getEnd());
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

    public String getLink() {
        return link;
    }

    /**
     * Get the display date. The resulting string is of the following format:
     *
     * dd/MM HH:mm tot [dd/MM] HH:mm
     *
     * The second date is only shown of it is not the same date as the first.
     *
     * @param context The context, to access localized string formatters.
     *
     * @return The text to display.
     */
    public String getDisplayDate(Context context) {

        LocalDateTime start = getLocalStart();
        LocalDateTime end = getLocalEnd();

        if (start == null && end == null) {
            return context.getString(R.string.sko_artist_time_none);
        }

        DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern(context.getString(R.string.formatter_sko_artist_full));
        DateTimeFormatter shortFormatter = DateTimeFormatter.ofPattern(context.getString(R.string.formatter_sko_artist_hour_only));

        if (start == null) {
            return context.getString(R.string.sko_artist_time_end, end.format(fullFormatter));
        } else if (end == null) {
            return context.getString(R.string.sko_artist_time_start, start.format(fullFormatter));
        } else {
            // Both are not null.
            String startString = start.format(fullFormatter);
            String endString;
            if (start.getDayOfMonth() == end.getDayOfMonth()) {
                endString = end.format(shortFormatter);
            } else {
                endString = end.format(fullFormatter);
            }

            return context.getString(R.string.sko_artist_time, startString, endString);
        }
    }

    public Intent addToCalendarIntent() {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, LOCATION)
                .putExtra(CalendarContract.Events.TITLE, getName())
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        if (getStart() != null) {
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getStart().toInstant().toEpochMilli());
        }
        if (getEnd() != null) {
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, getEnd().toInstant().toEpochMilli());
        }
        return intent;
    }

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
        dest.writeString(this.link);
    }

    public Artist() {
    }

    protected Artist(Parcel in) {
        this.name = in.readString();
        this.start = (OffsetDateTime) in.readSerializable();
        this.end = (OffsetDateTime) in.readSerializable();
        this.banner = in.readString();
        this.image = in.readString();
        this.content = in.readString();
        this.stage = in.readString();
        this.link = in.readString();
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel source) {
            return new Artist(source);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}