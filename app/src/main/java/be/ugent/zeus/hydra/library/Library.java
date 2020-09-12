package be.ugent.zeus.hydra.library;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import be.ugent.zeus.hydra.common.converter.IntBoolean;
import be.ugent.zeus.hydra.common.utils.NetworkUtils;
import com.squareup.moshi.Json;

/**
 * Model for a library.
 * <p>
 * A library is uniquely defined by it's code. The equals/hashCode methods operate on this assumption.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings("WeakerAccess")
public final class Library implements Parcelable {
    
    private static final String FALLBACK_HEADER = "https://picsum.photos/800/450?image=1073";
    private static final String FALLBACK_HEADER_SMALL = "https://picsum.photos/400/225?image=1073";
    private String department;
    private String email;
    private List<String> address;
    private String name;
    @Json(name = "name_nl")
    private String nameDutch;
    @Json(name = "name_en")
    private String nameEnglish;
    private String code;
    private List<String> telephone;
    @IntBoolean
    private boolean active;
    @Json(name = "thumbnail_url")
    private String thumbnail;
    @Json(name = "image_url")
    private String image;
    @Json(name = "lat")
    private String latitude;
    @Json(name = "long")
    private String longitude;
    private List<String> comments;
    private String contact;
    private String campus;
    private String faculty;
    private String link;
    private boolean favourite;

    public Library() {
        // No-args constructor
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
        this.favourite = in.readByte() != 0;
        this.nameEnglish = in.readString();
        this.nameDutch = in.readString();
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
        Locale locale = Locale.getDefault();
        if (locale.getLanguage().equals(Locale.ENGLISH.getLanguage()) && !TextUtils.isEmpty(nameEnglish)) {
            return nameEnglish;
        } else if (locale.getLanguage().equals(new Locale("nl").getLanguage()) && !TextUtils.isEmpty(nameDutch)) {
            return nameDutch;
        } else {
            return name;
        }
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

    @VisibleForTesting
    public void setTestName(String name) {
        this.name = name;
        this.nameEnglish = name;
        this.nameDutch = name;
    }

    public String getCode() {
        return code;
    }

    @VisibleForTesting
    public void setCode(String code) {
        this.code = code;
    }

    public boolean isFacultyBib() {
        return this.name.contains("Faculteitsbibliotheek");
    }

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

    /**
     * @return Concatenated comments (no delimiter) or null if there are no comments.
     */
    public String getCommentsAsString() {
        if (comments == null) {
            return null;
        } else {
            //noinspection SimplifyStreamApiCallChains
            return comments.stream().collect(Collectors.joining());
        }
    }

    /**
     * @return Concatenated phones (semi-colon delimiter) or null if there are no phones.
     */
    public String getPhones() {
        if (getTelephone() == null) {
            return null;
        } else {
            //noinspection SimplifyStreamApiCallChains
            return getTelephone().stream().collect(Collectors.joining("; "));
        }
    }

    /**
     * This method returns the URL of the header image for this library. This method will account for things
     * like data-saving and absence of an image.
     *
     * @return The header image.
     */
    @NonNull
    public String getHeaderImage(Context context) {
        // If data-saving is enabled, use the thumbnail instead of the full image.
        if (NetworkUtils.isMeteredConnection(context)) {
            if (getThumbnail() == null || getThumbnail().isEmpty()) {
                return FALLBACK_HEADER_SMALL;
            } else {
                return getThumbnail();
            }
        } else {
            if (getImage() == null || getImage().isEmpty()) {
                if (getThumbnail() == null || getThumbnail().isEmpty()) {
                    return FALLBACK_HEADER;
                } else {
                    return getThumbnail();
                }
            } else {
                return getImage();
            }
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
            //noinspection SimplifyStreamApiCallChains
            return getAddress().stream().collect(Collectors.joining("\n"));
        }
    }

    /**
     * @return True if there is at least one telephone number.
     */
    public boolean hasTelephone() {
        return getTelephone() != null && !getTelephone().isEmpty();
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
        dest.writeByte(this.favourite ? (byte) 1 : (byte) 0);
        dest.writeString(this.nameEnglish);
        dest.writeString(this.nameDutch);
    }
}
