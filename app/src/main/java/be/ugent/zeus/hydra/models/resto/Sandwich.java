package be.ugent.zeus.hydra.models.resto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by feliciaan on 04/02/16.
 */
public class Sandwich implements Parcelable {
    public String name;
    public ArrayList<String> ingredients;
    public String price_small;
    public String price_medium;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeStringList(this.ingredients);
        dest.writeString(this.price_small);
        dest.writeString(this.price_medium);
    }

    public Sandwich() {
    }

    protected Sandwich(Parcel in) {
        this.name = in.readString();
        this.ingredients = in.createStringArrayList();
        this.price_small = in.readString();
        this.price_medium = in.readString();
    }

    public static final Parcelable.Creator<Sandwich> CREATOR = new Parcelable.Creator<Sandwich>() {
        @Override
        public Sandwich createFromParcel(Parcel source) {
            return new Sandwich(source);
        }

        @Override
        public Sandwich[] newArray(int size) {
            return new Sandwich[size];
        }
    };
}
