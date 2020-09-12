package be.ugent.zeus.hydra.resto.meta;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * A restaurant.
 *
 * @author feliciaan
 */
@SuppressWarnings("WeakerAccess")
public final class Resto implements Parcelable {
    
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String type;
    @Nullable
    private String endpoint;

    protected Resto(Parcel in) {
        name = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        type = in.readString();
        endpoint = in.readString();
    }

    public Resto() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(type);
        dest.writeString(endpoint);
    }

    @Override
    public int describeContents() {
        return 0;
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

    @Nullable
    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resto resto = (Resto) o;
        return Double.compare(resto.latitude, latitude) == 0 &&
                Double.compare(resto.longitude, longitude) == 0 &&
                Objects.equals(name, resto.name) &&
                Objects.equals(address, resto.address) &&
                Objects.equals(type, resto.type) &&
                Objects.equals(endpoint, resto.endpoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, latitude, longitude, type, endpoint);
    }

    public static final Creator<Resto> CREATOR = new Creator<Resto>() {
        @Override
        public Resto createFromParcel(Parcel in) {
            return new Resto(in);
        }

        @Override
        public Resto[] newArray(int size) {
            return new Resto[size];
        }
    };
}
