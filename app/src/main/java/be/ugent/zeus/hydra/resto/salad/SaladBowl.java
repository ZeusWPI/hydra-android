package be.ugent.zeus.hydra.resto.salad;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * @author Niko Strijbol
 */
public class SaladBowl implements Parcelable {
    
    private String name;
    private String description;
    private String price;
    
    public SaladBowl() {}

    protected SaladBowl(Parcel in) {
        name = in.readString();
        description = in.readString();
        price = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<SaladBowl> CREATOR = new Creator<SaladBowl>() {
        @Override
        public SaladBowl createFromParcel(Parcel in) {
            return new SaladBowl(in);
        }

        @Override
        public SaladBowl[] newArray(int size) {
            return new SaladBowl[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaladBowl saladBowl = (SaladBowl) o;
        return Objects.equals(name, saladBowl.name) &&
                Objects.equals(description, saladBowl.description) &&
                Objects.equals(price, saladBowl.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, price);
    }
}
