package be.ugent.zeus.hydra.resto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import java9.util.Objects;
import org.threeten.bp.LocalDate;


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
    private transient List<RestoMeal> soups;
    private List<String> vegetables;
    private String message;

    @SuppressWarnings("unused") // Moshi uses this.
    public RestoMenu() {}

    /**
     * Sort the meals available in the menu.
     */
    private void fillCategories() {
        soups = new ArrayList<>();
        mainDishes = new ArrayList<>();

        for(RestoMeal meal : meals) {
            if (meal.getKind() != null && meal.getKind().equals("soup")) {
                soups.add(meal);
            } else {
                mainDishes.add(meal);
            }
        }
    }

    /**
     * @return If the resto is open.
     */
    public boolean isOpen() {
        return open;
    }

    /**
     * @see #isOpen()
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

    public List<RestoMeal> getSoups() {
        if (soups == null) {
            fillCategories();
        }
        return soups;
    }

    public List<RestoMeal> getMainDishes() {
        if (mainDishes == null) {
            fillCategories();
        }
        return mainDishes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    protected RestoMenu(Parcel in) {
        open = in.readByte() != 0;
        meals = in.createTypedArrayList(RestoMeal.CREATOR);
        vegetables = in.createStringArrayList();
        message = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (open ? 1 : 0));
        dest.writeTypedList(meals);
        dest.writeStringList(vegetables);
        dest.writeString(message);
    }

    public static final Creator<RestoMenu> CREATOR = new Creator<RestoMenu>() {
        @Override
        public RestoMenu createFromParcel(Parcel in) {
            return new RestoMenu(in);
        }

        @Override
        public RestoMenu[] newArray(int size) {
            return new RestoMenu[size];
        }
    };

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
        return Objects.hash(open, date, meals, mainDishes, soups, vegetables, message);
    }
}