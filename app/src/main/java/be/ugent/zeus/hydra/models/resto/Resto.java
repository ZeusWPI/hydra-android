package be.ugent.zeus.hydra.models.resto;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.utils.Objects;

import java.io.Serializable;

/**
 * A resto.
 * Created by feliciaan on 04/02/16.
 */
public class Resto implements Parcelable, Serializable {

    public String name;
    public String address;
    public double latitude;
    public double longitude;
    public String type;

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

    protected Resto(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.type = in.readString();
    }

    @DrawableRes
    public int getTypeIcon() {
        switch (this.type) {

            case "cafetaria":
                return R.drawable.ic_local_cafe;
            case "club":
                return R.drawable.ic_restaurant;
            case "resto":
            default:
                return R.drawable.ic_local_dining;
        }
    }

    public static final Parcelable.Creator<Resto> CREATOR = new Parcelable.Creator<Resto>() {
        @Override
        public Resto createFromParcel(Parcel source) {
            return new Resto(source);
        }

        @Override
        public Resto[] newArray(int size) {
            return new Resto[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resto resto = (Resto) o;
        return Double.compare(resto.latitude, latitude) == 0 &&
                Double.compare(resto.longitude, longitude) == 0 &&
                Objects.equals(name, resto.name) &&
                Objects.equals(address, resto.address) &&
                Objects.equals(type, resto.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, latitude, longitude, type);
    }
}