package be.ugent.zeus.hydra.resto.extrafood;

import java.util.List;

import java9.util.Objects;

/**
 * Contains all extra food items.
 *
 * @author Niko Strijbol
 */
public final class ExtraFood {

    private List<Food> breakfast;
    private List<Food> desserts;
    private List<Food> drinks;

    List<Food> getBreakfast() {
        return breakfast;
    }

    List<Food> getDesserts() {
        return desserts;
    }

    List<Food> getDrinks() {
        return drinks;
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

    public void setDrinks(List<Food> drinks) {
        this.drinks = drinks;
    }

    public void setDesserts(List<Food> desserts) {
        this.desserts = desserts;
    }

    public void setBreakfast(List<Food> breakfast) {
        this.breakfast = breakfast;
    }
}
