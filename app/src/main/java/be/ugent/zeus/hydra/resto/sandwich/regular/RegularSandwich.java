package be.ugent.zeus.hydra.resto.sandwich.regular;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import com.squareup.moshi.Json;
import java9.util.Objects;

/**
 * Created by feliciaan on 04/02/16.
 */
public final class RegularSandwich implements Parcelable {

    private String name;
    private List<String> ingredients;
    @Json(name = "price_small")
    private String priceSmall;
    @Json(name = "price_medium")
    private String priceMedium;

    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
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

    protected RegularSandwich(Parcel in) {
        this.name = in.readString();
        this.ingredients = in.createStringArrayList();
        this.priceSmall = in.readString();
        this.priceMedium = in.readString();
    }

    public static final Parcelable.Creator<RegularSandwich> CREATOR = new Parcelable.Creator<RegularSandwich>() {
        @Override
        public RegularSandwich createFromParcel(Parcel source) {
            return new RegularSandwich(source);
        }

        @Override
        public RegularSandwich[] newArray(int size) {
            return new RegularSandwich[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegularSandwich sandwich = (RegularSandwich) o;
        return Objects.equals(name, sandwich.name) &&
                Objects.equals(ingredients, sandwich.ingredients) &&
                Objects.equals(priceSmall, sandwich.priceSmall) &&
                Objects.equals(priceMedium, sandwich.priceMedium);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, ingredients, priceSmall, priceMedium);
    }

    @Override
    public String toString() {
        return name;
    }
}
