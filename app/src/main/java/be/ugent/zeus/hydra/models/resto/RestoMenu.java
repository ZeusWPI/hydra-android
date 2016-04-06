package be.ugent.zeus.hydra.models.resto;

import com.google.gson.annotations.JsonAdapter;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.Date;

import be.ugent.zeus.hydra.adapters.HomeCardAdapter;
import be.ugent.zeus.hydra.models.HomeCard;
import be.ugent.zeus.hydra.models.converters.RestoDateJsonAdapter;

/**
 * Created by feliciaan on 15/10/15.
 */
public class RestoMenu implements HomeCard{
    private boolean open;
    @JsonAdapter(RestoDateJsonAdapter.class)
    private Date date;
    private RestoMeals meals;
    private ArrayList<RestoMeal> sideDishes;
    private ArrayList<RestoMeal> mainDishes;
    private ArrayList<String> vegetables;

    public RestoMenu() {
    };

    public RestoMenu(boolean open, Date date, RestoMeals meals, ArrayList<String> vegetables) {
        this.open = open;
        this.date = date;
        this.meals = meals;
        this.vegetables = vegetables;
    }

    private void fillCategories() {
        sideDishes = new ArrayList<RestoMeal>();
        mainDishes = new ArrayList<RestoMeal>();

        for(RestoMeal meal : meals) {
            if (meal.getType().equals("side")) {
                sideDishes.add(meal);
            }
            if (meal.getType().equals("main")) {
                mainDishes.add(meal);
            }
        }
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public RestoMeals getMeals() {
        return meals;
    }

    public void setMeals(RestoMeals meals) {
        this.meals = meals;
        fillCategories();
    }

    public ArrayList<String> getVegetables() {
        return vegetables;
    }

    public void setVegetables(ArrayList<String> vegetables) {
        this.vegetables = vegetables;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ArrayList<RestoMeal> getSideDishes() {
        if (sideDishes == null) {
            fillCategories();
        }
        return sideDishes;
    }

    public ArrayList<RestoMeal> getMainDishes() {
        if (mainDishes == null) {
            fillCategories();
        }
        return mainDishes;
    }

    @Override
    public int getPriority() {
        DateTime jodadate = new DateTime(date);
        Duration duration = new Duration(jodadate, new DateTime());
        return (int) (1000 - (duration.getStandardDays()*50));
    }

    @Override
    public HomeCardAdapter.HomeType getCardType() {
        return HomeCardAdapter.HomeType.RESTO;
    }
}
