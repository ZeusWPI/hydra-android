package be.ugent.zeus.hydra.models.resto;

import android.os.Parcel;
import android.os.Parcelable;
import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.HomeCard;
import be.ugent.zeus.hydra.models.converters.RestoDateJsonAdapter;
import com.google.gson.annotations.JsonAdapter;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a menu for a single day.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public class RestoMenu implements HomeCard, Parcelable {

    private boolean open;
    @JsonAdapter(RestoDateJsonAdapter.class)
    private Date date;
    private List<RestoMeal> meals;
    private List<RestoMeal> sideDishes;
    private List<RestoMeal> mainDishes;
    private List<String> vegetables;

    public RestoMenu() {
    }

    /**
     * Sort the meals available in the menu.
     */
    private void fillCategories() {
        sideDishes = new ArrayList<>();
        mainDishes = new ArrayList<>();

        for(RestoMeal meal : meals) {
            if (meal.getType() == RestoMeal.MealType.SIDE) {
                sideDishes.add(meal);
            } else if (meal.getType() == RestoMeal.MealType.MAIN) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<RestoMeal> getSideDishes() {
        if (sideDishes == null) {
            fillCategories();
        }
        return sideDishes;
    }

    public List<RestoMeal> getMainDishes() {
        if (mainDishes == null) {
            fillCategories();
        }
        return mainDishes;
    }

    @Override
    public int getPriority() {
        DateTime jodadate = new DateTime(date);
        Duration duration = new Duration(new DateTime(), jodadate);
        return (int) (1000 - (duration.getStandardDays()*100));
    }

    @Override
    public HomeCardAdapter.HomeType getCardType() {
        return HomeCardAdapter.HomeType.RESTO;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.open ? (byte) 1 : (byte) 0);
        dest.writeLong(this.date != null ? this.date.getTime() : -1);
        dest.writeList(this.meals);
        dest.writeStringList(this.vegetables);
    }

    protected RestoMenu(Parcel in) {
        this.open = in.readByte() != 0;
        long tmpDate = in.readLong();
        this.date = tmpDate == -1 ? null : new Date(tmpDate);
        this.meals = new ArrayList<>();
        in.readList(this.meals,RestoMeal.class.getClassLoader());
        this.vegetables = in.createStringArrayList();
        fillCategories();
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
