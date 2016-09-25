package be.ugent.zeus.hydra.models.info;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * An info item.
 *
 * @author Juta
 * @author Niko Strijbol
 */
public class InfoItem implements Parcelable, Serializable {

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

    /**
     * @return The type of this info item.
     */
    public InfoType getType() {
        if (getUrlAndroid() != null) {
            return InfoType.EXTERNAL_APP;
        } else if (getHtml() != null) {
            return InfoType.INTERNAL;
        } else if (getSubContent() != null) {
            return InfoType.SUBLIST;
        } else {
            return InfoType.EXTERNAL_LINK;
        }
    }

    protected InfoItem(Parcel in) {
        title = in.readString();
        image = in.readString();
        html = in.readString();
        url = in.readString();
        urlAndroid = in.readString();
        in.readList(subContent, null);
    }

    public static final Creator<InfoItem> CREATOR = new Creator<InfoItem>() {
        @Override
        public InfoItem createFromParcel(Parcel in) {
            return new InfoItem(in);
        }

        @Override
        public InfoItem[] newArray(int size) {
            return new InfoItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(image);
        dest.writeString(html);
        dest.writeString(url);
        dest.writeString(urlAndroid);
        dest.writeList(subContent);
    }
}
