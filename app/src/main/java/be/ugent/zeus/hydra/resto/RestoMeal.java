/*
 * Copyright (c) 2023 The Hydra authors
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

import androidx.annotation.Nullable;

import java.util.List;

/**
 * Represents a meal.
 *
 * @author Niko Strijbol
 * @author Mitch
 */
public record RestoMeal(String name, @Nullable String price, @Nullable String type, String kind,
                        List<String> allergens) implements Parcelable {
    public static final String MENU_TYPE_COLD = "cold";

    private RestoMeal(Parcel in) {
        this(in.readString(), in.readString(), in.readString(), in.readString(), in.createStringArrayList());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.price);
        dest.writeString(this.type);
        dest.writeString(this.kind);
        dest.writeStringList(this.allergens);
    }

    public RestoMeal withPrice(String newPrice) {
        return new RestoMeal(name, newPrice, type, kind, allergens);
    }

    public RestoMeal withName(String newName) {
        return new RestoMeal(newName, price, type, kind, allergens);
    }

    public static final Parcelable.Creator<RestoMeal> CREATOR = new Parcelable.Creator<>() {
        @Override
        public RestoMeal createFromParcel(Parcel source) {
            return new RestoMeal(source);
        }

        @Override
        public RestoMeal[] newArray(int size) {
            return new RestoMeal[size];
        }
    };
}
