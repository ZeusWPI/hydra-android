package be.ugent.zeus.hydra.models.association;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java8.util.Objects;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Represents an association registered with the DSA.
 *
 * An association is identified by it's internal name. If the internal name is the same, the association is the same.
 * Both the hash and equals method are implemented for this class.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public final class Association implements Parcelable, Serializable {

    @SerializedName("internal_name")
    private String internalName;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("parent_association")
    private String parentAssociation;

    /**
     * @return A name for this association. If a full name is available, that is returned. If not, the display name is.
     */
    public String getName() {
        if (fullName != null) {
            return fullName;
        }
        return displayName;
    }

    @NonNull
    public String getInternalName() {
        return internalName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getParentAssociation() {
        return parentAssociation;
    }

    public String getImageLink() {
        return "https://zeus.ugent.be/hydra/api/2.0/association/logo/" + internalName.toLowerCase() + ".png";
    }

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

    protected Association(Parcel in) {
        internalName = in.readString();
        fullName = in.readString();
        displayName = in.readString();
        parentAssociation = in.readString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Association that = (Association) o;
        return Objects.equals(internalName, that.internalName);
    }

    @Override
    public int hashCode() {
        return java8.util.Objects.hash(internalName);
    }
}