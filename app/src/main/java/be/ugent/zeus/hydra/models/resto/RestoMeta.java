package be.ugent.zeus.hydra.models.resto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by feliciaan on 04/02/16.
 */
public class RestoMeta implements Parcelable {
    public ArrayList<RestoLocation> locations;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.locations);
    }

    public RestoMeta() {
    }

    protected RestoMeta(Parcel in) {
        this.locations = in.createTypedArrayList(RestoLocation.CREATOR);
    }

    public static final Parcelable.Creator<RestoMeta> CREATOR = new Parcelable.Creator<RestoMeta>() {
        @Override
        public RestoMeta createFromParcel(Parcel source) {
            return new RestoMeta(source);
        }

        @Override
        public RestoMeta[] newArray(int size) {
            return new RestoMeta[size];
        }
    };
}
