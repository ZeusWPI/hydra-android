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

package be.ugent.zeus.hydra.library.list;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

import be.ugent.zeus.hydra.library.Library;
import com.squareup.moshi.Json;

/**
 * A list of libraries.
 *
 * @author Niko Strijbol
 */
public final class LibraryList implements Parcelable {

    public static final Parcelable.Creator<LibraryList> CREATOR = new Parcelable.Creator<>() {
        @Override
        public LibraryList createFromParcel(Parcel source) {
            return new LibraryList(source);
        }

        @Override
        public LibraryList[] newArray(int size) {
            return new LibraryList[size];
        }
    };
    private String name;
    @Json(name = "libraries_total")
    private int totalLibraries;
    private List<Library> libraries;

    @SuppressWarnings("unused") // Used by Moshi.
    public LibraryList() {
    }

    private LibraryList(Parcel in) {
        this.name = in.readString();
        this.libraries = in.createTypedArrayList(Library.CREATOR);
        this.totalLibraries = in.readInt();
    }

    public String getName() {
        return name;
    }

    public List<Library> getLibraries() {
        return libraries;
    }

    @SuppressWarnings("unused")
    public int getTotalLibraries() {
        return totalLibraries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryList that = (LibraryList) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(libraries, that.libraries) &&
                totalLibraries == that.totalLibraries;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, libraries, totalLibraries);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeTypedList(this.libraries);
        dest.writeInt(totalLibraries);
    }
}