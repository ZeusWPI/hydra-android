package be.ugent.zeus.hydra.models.schamper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import be.ugent.zeus.hydra.models.converters.ISO8601DateJsonAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

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
    private String body;
    private String image;
    private String category;
    private String intro;
    private ArrayList<ArticleImage> images;

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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public List<ArticleImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<ArticleImage> images) {
        this.images = images;
    }

    public String getImage() {
        return image;
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
        dest.writeString(this.body);
        dest.writeString(this.image);
        dest.writeString(this.category);
        dest.writeString(this.intro);
        dest.writeTypedList(this.images);
    }

    public Article() {
    }

    protected Article(Parcel in) {
        this.title = in.readString();
        this.link = in.readString();
        long tmpPubDate = in.readLong();
        this.pubDate = tmpPubDate == -1 ? null : new Date(tmpPubDate);
        this.author = in.readString();
        this.body = in.readString();
        this.image = in.readString();
        this.category = in.readString();
        this.intro = in.readString();
        this.images = in.createTypedArrayList(ArticleImage.CREATOR);
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
}
