package be.ugent.zeus.hydra.data.models.association;

import android.os.Parcel;
import android.os.Parcelable;

import be.ugent.zeus.hydra.data.gson.ZonedThreeTenAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.TtbUtils;
import com.google.gson.annotations.JsonAdapter;
import java8.util.Objects;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class UgentNewsItem implements Serializable, Parcelable {

    private String description;

    private List<String> contributors;

    private String text;

    private List<String> subject = null;

    private String identifier;

    private String effective;
    private String language;
    private String rights;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime created;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime modified;

    private String expiration;

    private String title;

    private List<String> creators;

    public String getDescription() {
        return description;
    }

    public List<String> getContributors() {
        return contributors;
    }

    public String getText() {
        return text;
    }

    public List<String> getSubject() {
        return subject;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getEffective() {
        return effective;
    }

    public String getLanguage() {
        return language;
    }

    public String getRights() {
        return rights;
    }

    public LocalDateTime getLocalCreated() {
        return DateUtils.toLocalDateTime(getCreated());
    }

    public ZonedDateTime getCreated() {
        return created;
    }

    public LocalDateTime getLocalModified() {
        return DateUtils.toLocalDateTime(getModified());
    }

    public ZonedDateTime getModified() {
        return modified;
    }

    public String getExpiration() {
        return expiration;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getCreators() {
        return creators;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.description);
        dest.writeStringList(this.contributors);
        dest.writeString(this.text);
        dest.writeStringList(this.subject);
        dest.writeString(this.identifier);
        dest.writeString(this.effective);
        dest.writeString(this.language);
        dest.writeString(this.rights);
        dest.writeLong(TtbUtils.serialize(this.created));
        dest.writeLong(TtbUtils.serialize(this.modified));
        dest.writeString(this.expiration);
        dest.writeString(this.title);
        dest.writeStringList(this.creators);
    }

    public UgentNewsItem() {
    }

    protected UgentNewsItem(Parcel in) {
        this.description = in.readString();
        this.contributors = in.createStringArrayList();
        this.text = in.readString();
        this.subject = in.createStringArrayList();
        this.identifier = in.readString();
        this.effective = in.readString();
        this.language = in.readString();
        this.rights = in.readString();
        this.created = TtbUtils.unserialize(in.readLong());
        this.modified = TtbUtils.unserialize(in.readLong());
        this.expiration = in.readString();
        this.title = in.readString();
        this.creators = in.createStringArrayList();
    }

    public static final Creator<UgentNewsItem> CREATOR = new Creator<UgentNewsItem>() {
        @Override
        public UgentNewsItem createFromParcel(Parcel source) {
            return new UgentNewsItem(source);
        }

        @Override
        public UgentNewsItem[] newArray(int size) {
            return new UgentNewsItem[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UgentNewsItem that = (UgentNewsItem) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}