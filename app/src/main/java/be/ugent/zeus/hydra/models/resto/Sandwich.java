package be.ugent.zeus.hydra.models.resto;

import android.os.Parcel;
import android.os.Parcelable;
import be.ugent.zeus.hydra.utils.Objects;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by feliciaan on 04/02/16.
 */
public class Sandwich implements Parcelable, Serializable {

    public String name;
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
                Objects.equals(ingredients, sandwich.ingredients) &&
                Objects.equals(priceSmall, sandwich.priceSmall) &&
                Objects.equals(priceMedium, sandwich.priceMedium);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ingredients, priceSmall, priceMedium);
    }
}
