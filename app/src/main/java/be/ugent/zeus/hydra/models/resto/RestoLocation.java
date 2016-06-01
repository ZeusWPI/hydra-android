package be.ugent.zeus.hydra.models.resto;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by feliciaan on 04/02/16.
 */
public class RestoLocation implements Parcelable, Serializable {
    public String name;
    public String address;
    public double latitude;
    public double longitude;
    public String type; //TODO: parse types

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.address);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.type);
    }

    public RestoLocation() {
    }

    protected RestoLocation(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<RestoLocation> CREATOR = new Parcelable.Creator<RestoLocation>() {
        @Override
        public RestoLocation createFromParcel(Parcel source) {
            return new RestoLocation(source);
        }

        @Override
        public RestoLocation[] newArray(int size) {
            return new RestoLocation[size];
        }
    };
}
