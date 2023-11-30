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
import androidx.annotation.VisibleForTesting;
import androidx.core.os.ParcelCompat;

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
    
    private final boolean open;
    private final LocalDate date;
    private final List<RestoMeal> meals;
    private final List<String> vegetables;
    private final String message;
    
    private transient CategorizedMeals categorized;

    public RestoMenu(boolean open, LocalDate date, List<RestoMeal> meals, List<String> vegetables, String message) {
        this.open = open;
        this.date = date;
        this.meals = meals;
        this.vegetables = vegetables;
        this.message = message;
    }

    private RestoMenu(Parcel in) {
        this.open = in.readByte() != 0;
        this.date = ParcelCompat.readSerializable(in, LocalDate.class.getClassLoader(), LocalDate.class);
        this.meals = in.createTypedArrayList(RestoMeal.CREATOR);
        this.vegetables = in.createStringArrayList();
        this.message = in.readString();
    }

    /**
     * Sort the meals available in the menu.
     */
    private void fillCategoriesIfNeeded() {
        if (categorized != null) {
            return;
        }
        
        var soups = new ArrayList<RestoMeal>();
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
        
        this.categorized = new CategorizedMeals(mainDishes, coldDishes, soups);
    }
    
    @VisibleForTesting
    public RestoMenu withDate(LocalDate date) {
        return new RestoMenu(open, date, meals, vegetables, message);
    }

    /**
     * @return If the resto is closed.
     */
    public boolean isClosed() {
        return !open;
    }

    public List<String> vegetables() {
        return vegetables;
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
        ParcelCompat.writeBoolean(dest, this.open);
        dest.writeSerializable(this.date);
        dest.writeTypedList(this.meals);
        dest.writeStringList(this.vegetables);
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
