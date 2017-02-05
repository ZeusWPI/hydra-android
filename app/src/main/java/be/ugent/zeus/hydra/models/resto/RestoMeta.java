package be.ugent.zeus.hydra.models.resto;

import android.os.Parcel;
import android.os.Parcelable;
import java8.util.Objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by feliciaan on 04/02/16.
 */
public final class RestoMeta implements Parcelable, Serializable {

    public ArrayList<Resto> locations;

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
        this.locations = in.createTypedArrayList(Resto.CREATOR);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestoMeta restoMeta = (RestoMeta) o;
        return Objects.equals(locations, restoMeta.locations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locations);
    }
}
