/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.resto.extrafood;

import java.util.List;
import java.util.Objects;

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

    public void setBreakfast(List<Food> breakfast) {
        this.breakfast = breakfast;
    }

    List<Food> getDesserts() {
        return desserts;
    }

    public void setDesserts(List<Food> desserts) {
        this.desserts = desserts;
    }

    List<Food> getDrinks() {
        return drinks;
    }

    public void setDrinks(List<Food> drinks) {
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
}
