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

package be.ugent.zeus.hydra.resto;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.core.os.ParcelCompat;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a menu for a single day.
 *
 * @author feliciaan
 * @author Niko Strijbol
 */
public final class RestoMenu implements Parcelable {
    
    private final boolean open;
    private final LocalDate date;
    private final List<RestoMeal> meals;
    private final List<RestoMeal> vegetables2;
    private final String message;
    
    private transient CategorizedMeals categorized;

    public RestoMenu(boolean open, LocalDate date, List<RestoMeal> meals, List<RestoMeal> vegetables2, String message) {
        this.open = open;
        this.date = date;
        this.meals = meals;
        this.vegetables2 = vegetables2;
        this.message = message;
    }

    private RestoMenu(Parcel in) {
        this.open = in.readByte() != 0;
        this.date = ParcelCompat.readSerializable(in, LocalDate.class.getClassLoader(), LocalDate.class);
        this.meals = in.createTypedArrayList(RestoMeal.CREATOR);
        this.vegetables2 = in.createTypedArrayList(RestoMeal.CREATOR);
        this.message = in.readString();
    }

    /**
     * Sort the meals available in the menu.
     * This will also fix the soups.
     */
    private void fillCategoriesIfNeeded() {
        if (categorized != null) {
            return;
        }
        
        List<RestoMeal> soups = new ArrayList<>();
        var mainDishes = new ArrayList<RestoMeal>();
        var coldDishes = new ArrayList<RestoMeal>();

        for (RestoMeal meal : meals) {
            if (meal.kind() != null && meal.kind().equals("soup")) {
                soups.add(meal);
            } else {
                if (RestoMeal.MENU_TYPE_COLD.equals(meal.type())) {
                    coldDishes.add(meal);
                } else {
                    mainDishes.add(meal);
                }
            }
        }
        
        soups = fixSoups(soups);
        
        this.categorized = new CategorizedMeals(mainDishes, coldDishes, soups);
    }
    
    private static String removeSuffix(String word, String suffix) {
        if (word.endsWith(suffix)) {
            return word.substring(0, word.length() - suffix.length());
        } else {
            return word;
        }
    }
    
    private List<RestoMeal> fixSoups(List<RestoMeal> soups) {
        // TODO: this is rather ugly...
        List<RestoMeal> finalSoups = new ArrayList<>();
        var priceBig = "";
        for (RestoMeal soup: soups) {
            if (soup.name().endsWith("big") || soup.name().endsWith("groot")) {
                priceBig = soup.price();
            } else {
                var name = removeSuffix(soup.name(), " small");
                name = removeSuffix(name, " klein");
                finalSoups.add(soup.withName(name));
            }
        }
        final String suffix = priceBig;
        
        return finalSoups.stream().map(s -> s.withPrice(s.price() + " / " + suffix)).collect(Collectors.toList());
    }

    /**
     * @return If the resto is closed.
     */
    public boolean isClosed() {
        return !open;
    }

    public List<RestoMeal> vegetables() {
        return vegetables2;
    }

    public LocalDate date() {
        return date;
    }

    public String message() {
        return message;
    }

    @NonNull
    public List<RestoMeal> soups() {
        fillCategoriesIfNeeded();
        return categorized.soups();
    }

    @NonNull
    public List<RestoMeal> mainDishes() {
        fillCategoriesIfNeeded();
        return categorized.mainDishes();
    }
    
    @NonNull
    public List<RestoMeal> coldDishes() {
        fillCategoriesIfNeeded();
        return categorized.coldDishes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestoMenu restoMenu)) return false;
        return open == restoMenu.open &&
                Objects.equals(date, restoMenu.date) &&
                Objects.equals(meals, restoMenu.meals) &&
                Objects.equals(vegetables2, restoMenu.vegetables2) &&
                Objects.equals(message, restoMenu.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(open, date, meals, vegetables2, message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        ParcelCompat.writeBoolean(dest, this.open);
        dest.writeSerializable(this.date);
        dest.writeTypedList(this.meals);
        dest.writeTypedList(this.vegetables2);
        dest.writeString(this.message);
    }

    public static final Parcelable.Creator<RestoMenu> CREATOR = new Parcelable.Creator<>() {
        @Override
        public RestoMenu createFromParcel(Parcel source) {
            return new RestoMenu(source);
        }

        @Override
        public RestoMenu[] newArray(int size) {
            return new RestoMenu[size];
        }
    };
    
    public record CategorizedMeals(
            List<RestoMeal> mainDishes,
            List<RestoMeal> coldDishes,
            List<RestoMeal> soups
            ) {}
}
