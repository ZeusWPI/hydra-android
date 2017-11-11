package be.ugent.zeus.hydra.domain.models.sko;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import be.ugent.zeus.hydra.data.gson.ZonedThreeTenAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java8.util.Objects;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * A post on the timeline.
 *
 * @author Niko Strijbol
 */
public final class TimelinePost implements Serializable, Parcelable {

    //Use string def for the post type.
    public static final String PHOTO = "photo";
    public static final String LINK = "link";
    public static final String VIDEO = "video";
    public static final String EVENT = "event";

    @StringDef({PHOTO, LINK, VIDEO, EVENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PostType {}

    private String title;
    private String body;
    private String link;
    private String media;
    private String origin;
    @SerializedName("post_type")
    private String postType;
    private String poster;
    @SerializedName("created_at")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime createdAt;

    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getBody() {
        return body;
    }

    @Nullable
    public String getLink() {
        return link;
    }

    @Nullable
    public String getMedia() {
        return media;
    }

    public String getOrigin() {
        return origin;
    }

    @PostType
    public String getPostType() {
        return postType;
    }

    @Nullable
    public String getPoster() {
        return poster;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLocalCreatedAt() {
        return DateUtils.toLocalDateTime(getCreatedAt());
    }

    /**
     * Get the url to an image as cover. If the poster is not set and this is an image, the media is used.
     *
     * @return The url.
     */
    @Nullable
    public String getCoverMedia() {
        if(getPoster() != null) {
            return getPoster();
        } else {
            if(getPostType().equals(PHOTO) || getPostType().equals(EVENT)) {
                return getMedia();
            } else {
                return null;
            }
        }
    }

    /**
     * @return The text of the type to display.
     */
    @NonNull
    public String getDisplayType() {
        switch (getPostType()) {
            case PHOTO:
                return "Foto";
            case LINK:
                return "Link";
            case VIDEO:
                return "Video";
            case EVENT:
                return "Evenement";
            default:
                return "Andere";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.body);
        dest.writeString(this.link);
        dest.writeString(this.media);
        dest.writeString(this.origin);
        dest.writeString(this.postType);
        dest.writeString(this.poster);
        dest.writeSerializable(this.createdAt);
    }

    public TimelinePost() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimelinePost that = (TimelinePost) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(body, that.body) &&
                Objects.equals(postType, that.postType) &&
                Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, body, postType, createdAt);
    }

    protected TimelinePost(Parcel in) {
        this.title = in.readString();
        this.body = in.readString();
        this.link = in.readString();
        this.media = in.readString();
        this.origin = in.readString();
        this.postType = in.readString();
        this.poster = in.readString();
        this.createdAt = (ZonedDateTime) in.readSerializable();
    }

    public static final Parcelable.Creator<TimelinePost> CREATOR = new Parcelable.Creator<TimelinePost>() {
        @Override
        public TimelinePost createFromParcel(Parcel source) {
            return new TimelinePost(source);
        }

        @Override
        public TimelinePost[] newArray(int size) {
            return new TimelinePost[size];
        }
    };
}