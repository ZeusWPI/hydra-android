package be.ugent.zeus.hydra.sko.lineup;

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.CalendarContract;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.utils.DateUtils;
import java8.util.Objects;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.Serializable;

/**
 * An SKO artist.
 *
 * An artist is uniquely defined by his/her name, stage, start and stop time.
 *
 * @author Niko Strijbol
 */
public final class Artist implements Serializable, Parcelable {

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

        DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern(context.getString(R.string.formatter_sko_artist_full));
        DateTimeFormatter shortFormatter = DateTimeFormatter.ofPattern(context.getString(R.string.formatter_sko_artist_hour_only));

        String startString = start.format(fullFormatter);
        String endString;
        if (start.getDayOfMonth() == end.getDayOfMonth()) {
            endString = end.format(shortFormatter);
        } else {
            endString = end.format(fullFormatter);
        }

        return context.getString(R.string.sko_artist_time, startString, endString);
    }

    public Intent addToCalendarIntent() {
        return new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getStart().toInstant().toEpochMilli())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, getEnd().toInstant().toEpochMilli())
                .putExtra(CalendarContract.Events.TITLE, getName())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, LOCATION)
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
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

    protected Artist(Parcel in) {
        name = in.readString();
        banner = in.readString();
        image = in.readString();
        content = in.readString();
        stage = in.readString();
        link = in.readString();
        start = (OffsetDateTime) in.readSerializable();
        end = (OffsetDateTime) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(banner);
        dest.writeString(image);
        dest.writeString(content);
        dest.writeString(stage);
        dest.writeString(link);
        dest.writeSerializable(start);
        dest.writeSerializable(end);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Artist> CREATOR = new Creator<Artist>() {
        @Override
        public Artist createFromParcel(Parcel in) {
            return new Artist(in);
        }

        @Override
        public Artist[] newArray(int size) {
            return new Artist[size];
        }
    };
}