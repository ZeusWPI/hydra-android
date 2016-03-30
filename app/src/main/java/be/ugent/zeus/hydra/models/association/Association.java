package be.ugent.zeus.hydra.models.association;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by feliciaan on 04/02/16.
 */
public class Association implements Parcelable {
    public String internal_name;
    public String full_name;
    public String display_name;

    protected Association(Parcel in) {
        internal_name = in.readString();
        full_name = in.readString();
        display_name = in.readString();
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
    }
}
