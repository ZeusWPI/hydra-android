package be.ugent.zeus.hydra.models.schamper;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Niko Strijbol
 */
public class ArticleImage implements Parcelable, Serializable {

    private String url;
    private String caption;

    public ArticleImage(){}

    public String getUrl() {
        return url;
    }

    public String getCaption() {
        return caption;
    }

    public String getLargeUrl() {
        return Article.getLargeImage(getUrl());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
        dest.writeString(this.caption);
    }

    private ArticleImage(Parcel in) {
        this.url = in.readString();
        this.caption = in.readString();
    }

    public static final Parcelable.Creator<ArticleImage> CREATOR = new Parcelable.Creator<ArticleImage>() {
        @Override
        public ArticleImage createFromParcel(Parcel source) {
            return new ArticleImage(source);
        }

        @Override
        public ArticleImage[] newArray(int size) {
            return new ArticleImage[size];
        }
    };
}