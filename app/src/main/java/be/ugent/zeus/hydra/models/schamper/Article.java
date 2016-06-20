package be.ugent.zeus.hydra.models.schamper;

import android.os.Parcel;
import android.os.Parcelable;
import be.ugent.zeus.hydra.models.converters.ISO8601DateJsonAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by feliciaan on 16/06/16.
 */
public class Article implements Serializable, Parcelable {

    private String title;
    private String link;
    @JsonAdapter(ISO8601DateJsonAdapter.class)
    @SerializedName("pub_date")
    private Date pubDate;
    private String author;
    private String text;
    private String image;
    // TODO: find way to save read status

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.link);
        dest.writeLong(this.pubDate != null ? this.pubDate.getTime() : -1);
        dest.writeString(this.author);
        dest.writeString(this.text);
        dest.writeString(this.image);
    }

    public Article() {
    }

    protected Article(Parcel in) {
        this.title = in.readString();
        this.link = in.readString();
        long tmpPubDate = in.readLong();
        this.pubDate = tmpPubDate == -1 ? null : new Date(tmpPubDate);
        this.author = in.readString();
        this.text = in.readString();
        this.image = in.readString();
    }

    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };
}
