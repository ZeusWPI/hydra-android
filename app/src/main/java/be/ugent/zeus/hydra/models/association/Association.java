package be.ugent.zeus.hydra.models.association;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;

/**
 * Created by feliciaan on 04/02/16.
 */
public class Association implements Parcelable {
    private String internal_name;
    private String full_name;
    private String display_name;
    private String parent_association;

    protected Association(Parcel in) {
        internal_name = in.readString();
        full_name = in.readString();
        display_name = in.readString();
        parent_association = in.readString();
    }

    public String getName() {
        if (full_name != null) {
            return full_name;
        }
        return display_name;
    }

    public String getInternal_name() {
        return internal_name;
    }

    public void setInternal_name(String internal_name) {
        this.internal_name = internal_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getParent_association() {
        return parent_association;
    }

    public void setParent_association(String parent_association) {
        this.parent_association = parent_association;
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
        dest.writeString(internal_name);
        dest.writeString(full_name);
        dest.writeString(display_name);
        dest.writeString(parent_association);
    }

    public String getImageLink() {
        return "https://zeus.ugent.be/hydra/api/2.0/association/logo/" + internal_name.toLowerCase() + ".png";
    }
}
