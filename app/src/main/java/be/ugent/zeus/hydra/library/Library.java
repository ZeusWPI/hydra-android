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

package be.ugent.zeus.hydra.library;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.core.os.ParcelCompat;

import java.util.List;
import java.util.Locale;

import be.ugent.zeus.hydra.common.converter.IntBoolean;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import com.squareup.moshi.Json;
import io.soabase.recordbuilder.core.RecordBuilder;

/**
 * Model for a library.
 * <p>
 * A library is uniquely defined by it's code. The equals/hashCode methods operate on this assumption.
 *
 * @author Niko Strijbol
 */
@RecordBuilder
public record Library(
        String department,
        String email,
        List<String> address,
        String name,
        @Json(name = "name_nl") String nameDutch,
        @Json(name = "name_en") String nameEnglish,
        String code,
        List<String> telephone,
        @IntBoolean boolean active,
        @Json(name = "thumbnail_url") String thumbnail,
        @Json(name = "image_url") String image,
        @Json(name = "lat") String latitude,
        @Json(name = "long") String longitude,
        List<String> comments,
        String contact,
        String campus,
        String faculty,
        String link,
        boolean favourite
) implements Parcelable, LibraryBuilder.With {

    private static final String FALLBACK_HEADER = "https://picsum.photos/800/450?image=1073";
    private static final String FALLBACK_HEADER_SMALL = "https://picsum.photos/400/225?image=1073";

    private Library(Parcel in) {
        this(
                in.readString(),
                in.readString(),
                in.createStringArrayList(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.createStringArrayList(),
                ParcelCompat.readBoolean(in),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.createStringArrayList(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString(),
                ParcelCompat.readBoolean(in)
        );
    }

    @Override
    public String name() {
        Locale locale = Locale.getDefault();
        if (locale.getLanguage().equals(Locale.ENGLISH.getLanguage()) && !TextUtils.isEmpty(nameEnglish)) {
            return nameEnglish;
        } else if (locale.getLanguage().equals(new Locale("nl").getLanguage()) && !TextUtils.isEmpty(nameDutch)) {
            return nameDutch;
        } else {
            return name;
        }
    }

    public static final Creator<Library> CREATOR = new Creator<>() {
        @Override
        public Library createFromParcel(Parcel source) {
            return new Library(source);
        }

        @Override
        public Library[] newArray(int size) {
            return new Library[size];
        }
    };

    public boolean isFacultyBib() {
        return this.name.contains("Faculteitsbibliotheek");
    }

    /**
     * @return Concatenated comments (no delimiter) or null if there are no comments.
     */
    public String commentsAsString() {
        if (comments == null) {
            return null;
        } else {
            return String.join("", comments);
        }
    }

    /**
     * @return Concatenated phones (semi-colon delimiter) or null if there are no phones.
     */
    public String getPhones() {
        if (telephone == null) {
            return null;
        } else {
            return String.join("; ", telephone);
        }
    }

    /**
     * This method returns the URL of the header image for this library. This method will account for things
     * like data-saving and absence of an image.
     *
     * @return The header image.
     */
    @NonNull
    public String headerImage(Context context) {
        // If data-saving is enabled, use the thumbnail instead of the full image.
        if (NetworkUtils.isMeteredConnection(context)) {
            if (thumbnail == null || thumbnail.isEmpty()) {
                return FALLBACK_HEADER_SMALL;
            } else {
                return thumbnail;
            }
        } else {
            if (image == null || image.isEmpty()) {
                if (thumbnail == null || thumbnail.isEmpty()) {
                    return FALLBACK_HEADER;
                } else {
                    return thumbnail;
                }
            } else {
                return image;
            }
        }
    }

    /**
     * @return Concatenated addresses (newline delimiter) or null if there is no address.
     */
    @NonNull
    public String addressAsString() {
        if (address == null) {
            return "";
        } else {
            return String.join("\n", address);
        }
    }

    /**
     * @return True if there is at least one telephone number.
     */
    public boolean hasTelephone() {
        return telephone != null && !telephone.isEmpty();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.department);
        dest.writeString(this.email);
        dest.writeStringList(this.address);
        dest.writeString(this.name);
        dest.writeString(this.nameDutch);
        dest.writeString(this.nameEnglish);
        dest.writeString(this.code);
        dest.writeStringList(this.telephone);
        ParcelCompat.writeBoolean(dest, this.active);
        dest.writeString(this.thumbnail);
        dest.writeString(this.image);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeStringList(this.comments);
        dest.writeString(this.contact);
        dest.writeString(this.campus);
        dest.writeString(this.faculty);
        dest.writeString(this.link);
        ParcelCompat.writeBoolean(dest, this.favourite);
    }
}
