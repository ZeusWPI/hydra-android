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

import com.squareup.moshi.Json;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * An info item.
 *
 * @author Juta
 * @author Niko Strijbol
 */
@RecordBuilder
public record InfoItem(
        String title,
        String image,
        String html,
        String url,
        @Json(name = "url-android") String urlAndroid,
        @Json(name = "subcontent") List<InfoItem> subContent
) implements Parcelable, InfoItemBuilder.With {

    private InfoItem(Parcel in) {
        this(
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.createTypedArrayList(CREATOR)
        );
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

    /**
     * @return The type of this info item.
     */
    public InfoType getType() {
        if (urlAndroid != null) {
            return InfoType.EXTERNAL_APP;
        } else if (html != null) {
            return InfoType.INTERNAL;
        } else if (subContent != null) {
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
}
