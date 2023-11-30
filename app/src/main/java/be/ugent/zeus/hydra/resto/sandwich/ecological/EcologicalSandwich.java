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

package be.ugent.zeus.hydra.resto.sandwich.ecological;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.core.os.ParcelCompat;

import java.time.LocalDate;
import java.util.List;

/**
 * Ecological sandwich of the week.
 */
public record EcologicalSandwich(
        String name,
        boolean vegan,
        List<String> ingredients,
        LocalDate start,
        LocalDate end
) implements Parcelable {

    private EcologicalSandwich(Parcel in) {
        this(
                in.readString(),
                ParcelCompat.readBoolean(in),
                in.createStringArrayList(),
                ParcelCompat.readSerializable(in, LocalDate.class.getClassLoader(), LocalDate.class),
                ParcelCompat.readSerializable(in, LocalDate.class.getClassLoader(), LocalDate.class)
        );
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        ParcelCompat.writeBoolean(dest, this.vegan);
        dest.writeStringList(this.ingredients);
        dest.writeSerializable(this.start);
        dest.writeSerializable(this.end);
    }

    public static final Creator<EcologicalSandwich> CREATOR = new Creator<>() {
        @Override
        public EcologicalSandwich createFromParcel(Parcel source) {
            return new EcologicalSandwich(source);
        }

        @Override
        public EcologicalSandwich[] newArray(int size) {
            return new EcologicalSandwich[size];
        }
    };
}
