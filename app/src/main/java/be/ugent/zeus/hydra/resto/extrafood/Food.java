package be.ugent.zeus.hydra.resto.extrafood;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Represents a food item. A food item is defined by the tuple ({@link #name}, {@link #price}). The {@link #equals(Object)} and
 * {@link #hashCode()} methods are implemented according to that tuple.
 *
 * @author Niko Strijbol
 */
public final class Food implements Parcelable {
    
    private String name;
    private String price;

    protected Food(Parcel in) {
        this.name = in.readString();
        this.price = in.readString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Food food = (Food) o;
        return Objects.equals(name, food.name) &&
                Objects.equals(price, food.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.price);
    }

    public static final Parcelable.Creator<Food> CREATOR = new Parcelable.Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel source) {
            return new Food(source);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };
}
