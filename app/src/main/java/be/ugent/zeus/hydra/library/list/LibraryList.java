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

    public static final Parcelable.Creator<LibraryList> CREATOR = new Parcelable.Creator<LibraryList>() {
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