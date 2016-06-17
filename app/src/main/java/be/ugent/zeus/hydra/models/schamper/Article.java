package be.ugent.zeus.hydra.models.schamper;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import be.ugent.zeus.hydra.models.converters.ISO8601DateJsonAdapter;

/**
 * Created by feliciaan on 16/06/16.
 */
public class Article {
    private String title;
    private String link;
    @JsonAdapter(ISO8601DateJsonAdapter.class)
    @SerializedName("pub_date")
    private Date pubDate;
    private String author;
    private String text;
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
}
