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

package be.ugent.zeus.hydra.association;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * Represents an association registered with the DSA.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public record Association(
        String abbreviation,
        String name,
        List<String> path,
        @Nullable String description,
        @Nullable String email,
        @Nullable String logo,
        @Nullable String website
) implements Parcelable {

    private Association(Parcel in) {
        this(
                in.readString(),
                in.readString(),
                in.createStringArrayList(),
                in.readString(),
                in.readString(),
                in.readString(),
                in.readString()
        );
    }

    public static Association unknown(String name) {
        return new Association(
                "unknown",
                name,
                Collections.emptyList(),
                "Onbekende vereniging",
                null,
                null,
                null
        );
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(abbreviation);
        dest.writeString(name);
        dest.writeStringList(path);
        dest.writeString(description);
        dest.writeString(email);
        dest.writeString(logo);
        dest.writeString(website);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Association> CREATOR = new Creator<>() {
        @Override
        public Association createFromParcel(Parcel in) {
            return new Association(in);
        }

        @Override
        public Association[] newArray(int size) {
            return new Association[size];
        }
    };
}
