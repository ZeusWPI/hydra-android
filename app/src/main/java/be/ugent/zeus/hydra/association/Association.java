package be.ugent.zeus.hydra.association;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Objects;

import be.ugent.zeus.hydra.common.network.Endpoints;
import com.squareup.moshi.Json;

/**
 * Represents an association registered with the DSA.
 *
 * An association is identified by it's internal name. If the internal name is the same, the association is the same.
 * Both the hash and equals method are implemented for this class.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public final class Association implements Parcelable {
    public static final Creator<Association> CREATOR = new Creator<Association>() {
        @Override
        public Association createFromParcel(Parcel in) {
            return new Association(in);
        }

        @Override
        public Association[] newArray(int size) {
            return new Association[size];
        }
    };
    private String abbreviation;
    private String name;
    private List<String> path;
    
    @Nullable
    private String description;
    private String email;
    @Nullable
    private String logo;
    @Nullable
    private String website;

    public Association() {
        // Moshi uses this!
    }

    protected Association(Parcel in) {
        internalName = in.readString();
        fullName = in.readString();
        displayName = in.readString();
        parentAssociation = in.readString();
    }

    /**
     * @return A name for this association. If a full name is available, that is returned. If not, the display name is.
     */
    public String getName() {
        if (fullName != null) {
            return fullName;
        }
        return displayName;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getWebsite() {
        return website;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(internalName);
        dest.writeString(fullName);
        dest.writeString(displayName);
        dest.writeString(parentAssociation);
    }

    /**
     * @return A name for this association. If a full name is available, that is returned. If not, the display name is.
     */
    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    @Nullable
    public String getImageLink() {
        return logo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Association that = (Association) o;
        return Objects.equals(abbreviation, that.abbreviation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(abbreviation);
    }
    
    public static Association unknown() {
        Association association = new Association();
        association.abbreviation = "unknown";
        association.name = "Onbekend";
        association.description = "Onbekende vereniging";
        return association;
    }
}
