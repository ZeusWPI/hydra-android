package be.ugent.zeus.hydra.resto.extrafood;

import android.os.Parcel;
import android.os.Parcelable;

import java8.util.Objects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Contains all extra food items.
 *
 * @author Niko Strijbol
 */
public final class ExtraFood implements Serializable, Parcelable {

    private ArrayList<Food> breakfast;
    private ArrayList<Food> desserts;
    private ArrayList<Food> drinks;

    public ArrayList<Food> getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(ArrayList<Food> breakfast) {
        this.breakfast = breakfast;
    }

    public ArrayList<Food> getDesserts() {
        return desserts;
    }

    public void setDesserts(ArrayList<Food> desserts) {
        this.desserts = desserts;
    }

    public ArrayList<Food> getDrinks() {
        return drinks;
    }

    public void setDrinks(ArrayList<Food> drinks) {
        this.drinks = drinks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtraFood extraFood = (ExtraFood) o;
        return Objects.equals(breakfast, extraFood.breakfast) &&
                Objects.equals(desserts, extraFood.desserts) &&
                Objects.equals(drinks, extraFood.drinks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(breakfast, desserts, drinks);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.breakfast);
        dest.writeTypedList(this.desserts);
        dest.writeTypedList(this.drinks);
    }

    public ExtraFood() {
    }

    protected ExtraFood(Parcel in) {
        this.breakfast = in.createTypedArrayList(Food.CREATOR);
        this.desserts = in.createTypedArrayList(Food.CREATOR);
        this.drinks = in.createTypedArrayList(Food.CREATOR);
    }

    public static final Parcelable.Creator<ExtraFood> CREATOR = new Parcelable.Creator<ExtraFood>() {
        @Override
        public ExtraFood createFromParcel(Parcel source) {
            return new ExtraFood(source);
        }

        @Override
        public ExtraFood[] newArray(int size) {
            return new ExtraFood[size];
        }
    };
}