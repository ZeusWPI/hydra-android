package be.ugent.zeus.hydra.resto;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a menu for a single day.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public final class RestoMenu implements Parcelable {
    
    private boolean open;
    private LocalDate date;
    private List<RestoMeal> meals;
    private transient List<RestoMeal> mainDishes;
    private transient List<RestoMeal> coldDishes;
    private transient List<RestoMeal> soups;
    private List<String> vegetables;
    private String message;

    public RestoMenu() {
    }

    protected RestoMenu(Parcel in) {
        this.open = in.readByte() != 0;
        this.date = (LocalDate) in.readSerializable();
        this.meals = in.createTypedArrayList(RestoMeal.CREATOR);
        this.vegetables = in.createStringArrayList();
        this.message = in.readString();
    }

    /**
     * Sort the meals available in the menu.
     */
    private void fillCategories() {
        soups = new ArrayList<>();
        mainDishes = new ArrayList<>();
        coldDishes = new ArrayList<>();

        for (RestoMeal meal : meals) {
            if (meal.getKind() != null && meal.getKind().equals("soup")) {
                soups.add(meal);
            } else {
                if (RestoMeal.MENU_TYPE_COLD.equals(meal.getType())) {
                    coldDishes.add(meal);
                } else {
                    mainDishes.add(meal);
                }
            }
        }
    }

    /**
     * @return If the resto is closed.
     */
    public boolean isClosed() {
        return !open;
    }

    /**
     * @see #isClosed()
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     * @return The meals available on this menu.
     */
    public List<RestoMeal> getMeals() {
        return meals;
    }

    public void setMeals(List<RestoMeal> meals) {
        this.meals = meals;
        fillCategories();
    }

    public List<String> getVegetables() {
        return vegetables;
    }

    public void setVegetables(List<String> vegetables) {
        this.vegetables = vegetables;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @NonNull
    public List<RestoMeal> getSoups() {
        if (soups == null) {
            fillCategories();
        }
        return soups;
    }

    @NonNull
    public List<RestoMeal> getMainDishes() {
        if (mainDishes == null) {
            fillCategories();
        }
        return mainDishes;
    }
    
    @NonNull
    public List<RestoMeal> getColdDishes() {
        if (coldDishes == null) {
            fillCategories();
        }
        return coldDishes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestoMenu)) return false;
        RestoMenu restoMenu = (RestoMenu) o;
        return open == restoMenu.open &&
                Objects.equals(date, restoMenu.date) &&
                Objects.equals(meals, restoMenu.meals) &&
                Objects.equals(vegetables, restoMenu.vegetables) &&
                Objects.equals(message, restoMenu.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(open, date, meals, vegetables, message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.open ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.date);
        dest.writeTypedList(this.meals);
        dest.writeStringList(this.vegetables);
        dest.writeString(this.message);
    }

    public static final Parcelable.Creator<RestoMenu> CREATOR = new Parcelable.Creator<RestoMenu>() {
        @Override
        public RestoMenu createFromParcel(Parcel source) {
            return new RestoMenu(source);
        }

        @Override
        public RestoMenu[] newArray(int size) {
            return new RestoMenu[size];
        }
    };
}
