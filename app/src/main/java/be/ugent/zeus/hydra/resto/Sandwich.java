package be.ugent.zeus.hydra.resto;

import android.os.Parcel;
import android.os.Parcelable;
import java8.util.Objects;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by feliciaan on 04/02/16.
 */
public final class Sandwich implements Parcelable, Serializable {

    private String name;
    private ArrayList<String> ingredients;
    @SerializedName("price_small")
    private String priceSmall;
    @SerializedName("price_medium")
    private String priceMedium;

    public String getName() {
        return name;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public String getPriceSmall() {
        return priceSmall;
    }

    public String getPriceMedium() {
        return priceMedium;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeStringList(this.ingredients);
        dest.writeString(this.priceSmall);
        dest.writeString(this.priceMedium);
    }

    protected Sandwich(Parcel in) {
        this.name = in.readString();
        this.ingredients = in.createStringArrayList();
        this.priceSmall = in.readString();
        this.priceMedium = in.readString();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sandwich sandwich = (Sandwich) o;
        return Objects.equals(name, sandwich.name) &&
                java8.util.Objects.equals(ingredients, sandwich.ingredients) &&
                java8.util.Objects.equals(priceSmall, sandwich.priceSmall) &&
                java8.util.Objects.equals(priceMedium, sandwich.priceMedium);
    }

    @Override
    public int hashCode() {
        return java8.util.Objects.hash(name, ingredients, priceSmall, priceMedium);
    }

    @Override
    public String toString() {
        return name;
    }
}
