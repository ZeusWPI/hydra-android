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

package be.ugent.zeus.hydra.resto.meta;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.Objects;

/**
 * Created by feliciaan on 04/02/16.
 */
public final class RestoMeta implements Parcelable {
    
    public List<Resto> locations;

    @SuppressWarnings("unused") // Used by Moshi.
    public RestoMeta() {
    }

    private RestoMeta(Parcel in) {
        this.locations = in.createTypedArrayList(Resto.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.locations);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestoMeta restoMeta = (RestoMeta) o;
        return Objects.equals(locations, restoMeta.locations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locations);
    }

    public static final Parcelable.Creator<RestoMeta> CREATOR = new Parcelable.Creator<RestoMeta>() {
        @Override
        public RestoMeta createFromParcel(Parcel source) {
            return new RestoMeta(source);
        }

        @Override
        public RestoMeta[] newArray(int size) {
            return new RestoMeta[size];
        }
    };
}
