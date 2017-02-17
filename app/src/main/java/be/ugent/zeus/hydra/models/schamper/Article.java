package be.ugent.zeus.hydra.models.schamper;

import android.os.Parcel;
import android.os.Parcelable;
import be.ugent.zeus.hydra.models.converters.ZonedThreeTenAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import be.ugent.zeus.hydra.utils.TtbUtils;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;

/**
 * Model for a Schamper article.
 *
 * @author feliciaan
 */
public final class Article implements Serializable, Parcelable {

    private String title;
    private String link;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    @SerializedName("pub_date")
    private ZonedDateTime pubDate;
    private String author;
    private String text;
    private String image;
    private String category;

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public ZonedDateTime getPubDate() {
        return pubDate;
    }

    public LocalDateTime getLocalPubDate() {
        return DateUtils.toLocalDateTime(getPubDate());
    }

    public String getAuthor() {
        return author;
    }

    public String getText() {
        return text;
    }

    public String getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.link);
        dest.writeLong(TtbUtils.serialize(this.pubDate));
        dest.writeString(this.author);
        dest.writeString(this.text);
        dest.writeString(this.image);
        dest.writeString(this.category);
    }

    public Article() {
    }

    private Article(Parcel in) {
        this.title = in.readString();
        this.link = in.readString();
        this.pubDate = TtbUtils.unserialize(in.readLong());
        this.author = in.readString();
        this.text = in.readString();
        this.image = in.readString();
        this.category = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return java8.util.Objects.equals(link, article.link) &&
                java8.util.Objects.equals(pubDate, article.pubDate);
    }

    @Override
    public int hashCode() {
        return java8.util.Objects.hash(link, pubDate);
    }
}