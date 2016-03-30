package be.ugent.zeus.hydra.models.resto;

import com.google.gson.annotations.JsonAdapter;

import java.util.ArrayList;
import java.util.Date;

import be.ugent.zeus.hydra.models.converters.RestoDateJsonAdapter;

/**
 * Created by feliciaan on 15/10/15.
 */
public class RestoMenu {
    private boolean open;
    @JsonAdapter(RestoDateJsonAdapter.class)
    private Date date;
    private RestoMeals meals;
    private ArrayList<String> vegetables;

    public RestoMenu() {};

    public RestoMenu(boolean open, Date date, RestoMeals meals, ArrayList<String> vegetables) {
        this.open = open;
        this.date = date;
        this.meals = meals;
        this.vegetables = vegetables;
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
}
