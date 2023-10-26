/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.info;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

import com.squareup.moshi.Json;

/**
 * An info item.
 *
 * @author Juta
 * @author Niko Strijbol
 * @noinspection unused
 */
public final class InfoItem implements Parcelable {
    
    private String title;
    private String image;
    private String html;
    private String url;
    @Json(name = "url-android")
    private String urlAndroid;
    @Json(name = "subcontent")
    private List<InfoItem> subContent;

    public InfoItem() {
        // Used by Moshi.
    }

    /** @noinspection ProtectedMemberInFinalClass*/
    protected InfoItem(Parcel in) {
        title = in.readString();
        image = in.readString();
        html = in.readString();
        url = in.readString();
        urlAndroid = in.readString();
        subContent = in.createTypedArrayList(CREATOR);
    }

    public static final Creator<InfoItem> CREATOR = new Creator<>() {
        @Override
        public InfoItem createFromParcel(Parcel in) {
            return new InfoItem(in);
        }

        @Override
        public InfoItem[] newArray(int size) {
            return new InfoItem[size];
        }
    };

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

    public List<InfoItem> getSubContent() {
        return subContent;
    }

    public void setSubContent(List<InfoItem> subContent) {
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
        dest.writeTypedList(subContent);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InfoItem infoItem = (InfoItem) o;
        return Objects.equals(title, infoItem.title) &&
                Objects.equals(image, infoItem.image) &&
                Objects.equals(html, infoItem.html) &&
                Objects.equals(url, infoItem.url) &&
                Objects.equals(urlAndroid, infoItem.urlAndroid) &&
                Objects.equals(subContent, infoItem.subContent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, image, html, url, urlAndroid, subContent);
    }
}
