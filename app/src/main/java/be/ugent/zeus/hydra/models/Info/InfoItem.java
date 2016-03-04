package be.ugent.zeus.hydra.models.Info;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Juta on 03/03/2016.
 */
public class InfoItem {
    private String title;
    private String image;
    private String html;
    private String url;
    @SerializedName("url-android")
    private String urlAndroid;
    @SerializedName("subcontent")
    private InfoList subContent;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlAndroid() {
        return urlAndroid;
    }

    public void setUrlAndroid(String urlAndroid) {
        this.urlAndroid = urlAndroid;
    }

    public InfoList getSubContent() {
        return subContent;
    }

    public void setSubContent(InfoList subContent) {
        this.subContent = subContent;
    }
}
