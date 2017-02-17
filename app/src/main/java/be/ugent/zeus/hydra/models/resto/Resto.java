package be.ugent.zeus.hydra.models.resto;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * A restaurant.
 *
 * @author feliciaan
 */
public final class Resto implements Parcelable, Serializable {

    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String type;

    protected Resto(Parcel in) {
        this.name = in.readString();
        this.address = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.type = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resto resto = (Resto) o;
        return Double.compare(resto.latitude, latitude) == 0 &&
                Double.compare(resto.longitude, longitude) == 0 &&
                java8.util.Objects.equals(name, resto.name) &&
                java8.util.Objects.equals(address, resto.address) &&
                java8.util.Objects.equals(type, resto.type);
    }

    @Override
    public int hashCode() {
        return java8.util.Objects.hash(name, address, latitude, longitude, type);
    }

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
}