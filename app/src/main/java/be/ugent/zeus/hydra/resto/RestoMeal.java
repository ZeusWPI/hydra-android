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

import java.util.Objects;

/**
 * Represents a meal.
 *
 * @author Niko Strijbol
 * @author Mitch
 */
public final class RestoMeal implements Parcelable {
    public static String MENU_TYPE_MAIN = "main";
    public static String MENU_TYPE_SIDE = "side";
    public static String MENU_TYPE_COLD = "cold";
    
    private String name;
    private String price;
    private String type;
    private String kind;

    public RestoMeal() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
    }

    private RestoMeal(Parcel in) {
        this.name = in.readString();
        this.price = in.readString();
        this.type = in.readString();
        this.kind = in.readString();
    }

    public static final Parcelable.Creator<RestoMeal> CREATOR = new Parcelable.Creator<RestoMeal>() {
        @Override
        public RestoMeal createFromParcel(Parcel source) {
            return new RestoMeal(source);
        }

        @Override
        public RestoMeal[] newArray(int size) {
            return new RestoMeal[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestoMeal restoMeal = (RestoMeal) o;
        return Objects.equals(name, restoMeal.name) &&
                Objects.equals(price, restoMeal.price) &&
                Objects.equals(type, restoMeal.type) &&
                Objects.equals(kind, restoMeal.kind);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price, type, kind);
    }
}
