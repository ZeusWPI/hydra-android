package be.ugent.zeus.hydra.domain.models.schamper;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import be.ugent.zeus.hydra.data.gson.ZonedThreeTenAdapter;
import be.ugent.zeus.hydra.utils.DateUtils;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;

/**
 * Created by feliciaan on 16/06/16.
 */
public final class Article implements Serializable, Parcelable {

    private String title;
    private String link;
    @JsonAdapter(ZonedThreeTenAdapter.class)
    @SerializedName("pub_date")
    private ZonedDateTime pubDate;
    private String author;
    private String body;
    private String image;
    private String category;
    private String intro;
    @SerializedName("category_color")
    private String categoryColour;

    public Article() {
        // Empty constructor for Gson
    }

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

    public String getBody() {
        return body;
    }

    public String getCategory() {
        return category;
    }

    public String getIntro() {
        return intro;
    }

    public String getImage() {
        return image;
    }

    public String getCategoryColour() {
        return categoryColour;
    }

    public boolean hasCategoryColour() {
        return !TextUtils.isEmpty(categoryColour);
    }

    public String getLargeImage() {
        if(getImage() != null) {
            return getLargeImage(getImage());
        } else {
            return null;
        }
    }

    public static String getLargeImage(String url) {
        return url.replace("/regulier/", "/preview/");
    }

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

    protected Article(Parcel in) {
        title = in.readString();
        link = in.readString();
        author = in.readString();
        body = in.readString();
        image = in.readString();
        category = in.readString();
        intro = in.readString();
        categoryColour = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(link);
        dest.writeString(author);
        dest.writeString(body);
        dest.writeString(image);
        dest.writeString(category);
        dest.writeString(intro);
        dest.writeString(categoryColour);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}