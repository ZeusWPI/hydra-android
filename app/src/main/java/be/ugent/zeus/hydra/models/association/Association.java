package be.ugent.zeus.hydra.models.association;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by feliciaan on 04/02/16.
 */
public class Association implements Parcelable {
    @SerializedName("internal_name")
    private String internalName;
    @SerializedName("full_name")
    private String fullName;
    @SerializedName("display_name")
    private String displayName;
    @SerializedName("parent_association")
    private String parentAssociation;

    protected Association(Parcel in) {
        internalName = in.readString();
        fullName = in.readString();
        displayName = in.readString();
        parentAssociation = in.readString();
    }

    public String getName() {
        if (fullName != null) {
            return fullName;
        }
        return displayName;
    }

    public String getInternalName() {
        return internalName;
    }

    public void setInternalName(String internalName) {
        this.internalName = internalName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getParentAssociation() {
        return parentAssociation;
    }

    public void setParentAssociation(String parentAssociation) {
        this.parentAssociation = parentAssociation;
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

    public String getImageLink() {
        return "https://zeus.ugent.be/hydra/api/2.0/association/logo/" + internalName.toLowerCase() + ".png";
    }
}
