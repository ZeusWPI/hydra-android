package be.ugent.zeus.hydra.data.models.library;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import be.ugent.zeus.hydra.data.gson.BooleanJsonAdapter;
import be.ugent.zeus.hydra.data.gson.ZonedThreeTenAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import java8.util.Objects;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;
import org.threeten.bp.ZonedDateTime;

import java.io.Serializable;
import java.util.List;

/**
 * Model for a library.
 *
 * A library is uniquely defined by it's code. The equals/hashCode methods operate on this assumption.
 *
 * @author Niko Strijbol
 */
public final class Library implements Serializable, Parcelable {

    private String department;
    private String email;
    private List<String> address;
    private String name;
    private String code;
    private List<String> telephone;
    @JsonAdapter(BooleanJsonAdapter.class)
    private boolean active;
    @SerializedName("thumbnail_url")
    private String thumbnail;
    @SerializedName("image_url")
    private String image;
    @SerializedName("lat")
    private String latitude;
    @SerializedName("long")
    private String longitude;
    @SerializedName("comments")
    private List<String> comments;
    private String contact;
    private String campus;
    private String faculty;
    private String link;
    @SerializedName("created_at")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime createdAt;
    @SerializedName("updated_at")
    @JsonAdapter(ZonedThreeTenAdapter.class)
    private ZonedDateTime updatedAt;

    private boolean favourite;

    public Library() {
        // No-args constructor
    }

    public String getDepartment() {
        return department;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public boolean isFacultyBib() { return this.name.contains("Faculteitsbibliotheek"); }

    public List<String> getTelephone() {
        return telephone;
    }

    public boolean isActive() {
        return active;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getImage() {
        return image;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getContact() {
        return contact;
    }

    public String getCampus() {
        return campus;
    }

    public String getFaculty() {
        return faculty;
    }

    public String getLink() {
        return link;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isFavourite() {
        return favourite;
    }

    /**
     * @return Concatenated comments (no delimiter) or null if there are no comments.
     */
    public String getCommentsAsString() {
        if (comments == null) {
            return null;
        } else {
            return StreamSupport.stream(comments).collect(Collectors.joining());
        }
    }

    /**
     * @return Concatenated phones (semi-colon delimiter) or null if there are no phones.
     */
    public String getPhones() {
        if (getTelephone() == null) {
            return null;
        } else {
            return StreamSupport.stream(getTelephone()).collect(Collectors.joining("; "));
        }
    }

    /**
     * @return The URL to the image of this library, or an URL to a placeholder.
     */
    @NonNull
    public String getEnsuredImage() {
        if (TextUtils.isEmpty(getImage())) {
            return "https://unsplash.it/1600/900?image=1073";
        } else {
            return getImage();
        }
    }

    /**
     * @return Concatenated addresses (newline delimiter) or null if there is no address.
     */
    @NonNull
    public String addressAsString() {
        if (getAddress() == null) {
            return "";
        } else {
            return StreamSupport.stream(getAddress()).collect(Collectors.joining("\n"));
        }
    }

    /**
     * @return True if there is at least one telephone number.
     */
    public boolean hasTelephone() {
        return getTelephone() != null && getTelephone().size() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Library library = (Library) o;
        return Objects.equals(code, library.code) && Objects.equals(favourite, library.favourite);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, favourite);
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
        dest.writeString(this.code);
        dest.writeStringList(this.telephone);
        dest.writeByte(this.active ? (byte) 1 : (byte) 0);
        dest.writeString(this.thumbnail);
        dest.writeString(this.image);
        dest.writeString(this.latitude);
        dest.writeString(this.longitude);
        dest.writeStringList(this.comments);
        dest.writeString(this.contact);
        dest.writeString(this.campus);
        dest.writeString(this.faculty);
        dest.writeString(this.link);
        dest.writeSerializable(this.createdAt);
        dest.writeSerializable(this.updatedAt);
        dest.writeByte(this.favourite ? (byte) 1 : (byte) 0);
    }

    protected Library(Parcel in) {
        this.department = in.readString();
        this.email = in.readString();
        this.address = in.createStringArrayList();
        this.name = in.readString();
        this.code = in.readString();
        this.telephone = in.createStringArrayList();
        this.active = in.readByte() != 0;
        this.thumbnail = in.readString();
        this.image = in.readString();
        this.latitude = in.readString();
        this.longitude = in.readString();
        this.comments = in.createStringArrayList();
        this.contact = in.readString();
        this.campus = in.readString();
        this.faculty = in.readString();
        this.link = in.readString();
        this.createdAt = (ZonedDateTime) in.readSerializable();
        this.updatedAt = (ZonedDateTime) in.readSerializable();
        this.favourite = in.readByte() != 0;
    }

    public static final Creator<Library> CREATOR = new Creator<Library>() {
        @Override
        public Library createFromParcel(Parcel source) {
            return new Library(source);
        }

        @Override
        public Library[] newArray(int size) {
            return new Library[size];
        }
    };
}