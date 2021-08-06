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
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * A restaurant.
 *
 * @author feliciaan
 */
@SuppressWarnings("WeakerAccess")
public final class Resto implements Parcelable {
    
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String type;
    @Nullable
    private String endpoint;

    protected Resto(Parcel in) {
        name = in.readString();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        type = in.readString();
        endpoint = in.readString();
    }

    public Resto() {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(type);
        dest.writeString(endpoint);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }

    @Nullable
    public String getEndpoint() {
        return endpoint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resto resto = (Resto) o;
        return Double.compare(resto.latitude, latitude) == 0 &&
                Double.compare(resto.longitude, longitude) == 0 &&
                Objects.equals(name, resto.name) &&
                Objects.equals(address, resto.address) &&
                Objects.equals(type, resto.type) &&
                Objects.equals(endpoint, resto.endpoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, latitude, longitude, type, endpoint);
    }

    public static final Creator<Resto> CREATOR = new Creator<Resto>() {
        @Override
        public Resto createFromParcel(Parcel in) {
            return new Resto(in);
        }

        @Override
        public Resto[] newArray(int size) {
            return new Resto[size];
        }
    };
}
