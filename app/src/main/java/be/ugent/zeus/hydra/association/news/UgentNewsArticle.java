package be.ugent.zeus.hydra.association.news;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import be.ugent.zeus.hydra.common.converter.DateTypeConverters;
import be.ugent.zeus.hydra.utils.DateUtils;
import java9.util.Objects;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;

/**
 * @author Niko Strijbol
 */
public final class UgentNewsArticle implements Parcelable {

    private String description;
    private List<String> contributors;
    private String text;
    private List<String> subject;
    private String identifier;
    private String effective;
    private String language;
    private String rights;
    private OffsetDateTime created;
    private OffsetDateTime modified;
    private String expiration;
    private String title;
    private List<String> creators;

    @SuppressWarnings("unused") // Moshi uses this!
    public UgentNewsArticle() {}

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

    public OffsetDateTime getCreated() {
        return created;
    }

    public LocalDateTime getLocalModified() {
        return DateUtils.toLocalDateTime(getModified());
    }

    public OffsetDateTime getModified() {
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

    public void setModified(OffsetDateTime modified) {
        this.modified = modified;
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
        dest.writeString(DateTypeConverters.fromOffsetDateTime(this.created));
        dest.writeString(DateTypeConverters.fromOffsetDateTime(this.modified));
        dest.writeString(this.expiration);
        dest.writeString(this.title);
        dest.writeStringList(this.creators);
    }

    protected UgentNewsArticle(Parcel in) {
        this.description = in.readString();
        this.contributors = in.createStringArrayList();
        this.text = in.readString();
        this.subject = in.createStringArrayList();
        this.identifier = in.readString();
        this.effective = in.readString();
        this.language = in.readString();
        this.rights = in.readString();
        this.created = DateTypeConverters.toOffsetDateTime(in.readString());
        this.modified = DateTypeConverters.toOffsetDateTime(in.readString());
        this.expiration = in.readString();
        this.title = in.readString();
        this.creators = in.createStringArrayList();
    }

    public static final Creator<UgentNewsArticle> CREATOR = new Creator<UgentNewsArticle>() {
        @Override
        public UgentNewsArticle createFromParcel(Parcel source) {
            return new UgentNewsArticle(source);
        }

        @Override
        public UgentNewsArticle[] newArray(int size) {
            return new UgentNewsArticle[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UgentNewsArticle that = (UgentNewsArticle) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }
}