package be.ugent.zeus.hydra.domain.models.library;

import android.os.Parcel;
import android.os.Parcelable;
import be.ugent.zeus.hydra.data.gson.ZonedThreeTenAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java8.util.Objects;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;
import java.util.List;

/**
 * A list of libraries.
 *
 * @author Niko Strijbol
 */
public final class LibraryList implements Serializable, Parcelable {

    private String name;

    @SerializedName("created_at")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime createdAt;

    @SerializedName("updated_at")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime updatedAt;

    private List<Library> libraries;

    public String getName() {
        return name;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Library> getLibraries() {
        return libraries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LibraryList that = (LibraryList) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(libraries, that.libraries);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, libraries);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
        dest.writeTypedList(this.libraries);
    }

    public LibraryList() {
    }

    protected LibraryList(Parcel in) {
        this.name = in.readString();
        this.createdAt = (ZonedDateTime) in.readSerializable();
        this.updatedAt = (ZonedDateTime) in.readSerializable();
        this.libraries = in.createTypedArrayList(Library.CREATOR);
    }

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
}