/*
 * Copyright (c) 2022 Niko Strijbol
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

package be.ugent.zeus.hydra.wpi.tap.cart;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Pair;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.ugent.zeus.hydra.common.converter.DateTypeConverters;

/**
 * @author Niko Strijbol
 */
public class CartStorage {
    
    private final List<Pair<Integer, Integer>> productIdsAndAmounts;
    private final OffsetDateTime lastEdited;

    public CartStorage(List<Pair<Integer, Integer>> productIds, OffsetDateTime lastEdited) {
        this.productIdsAndAmounts = productIds;
        this.lastEdited = lastEdited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartStorage that = (CartStorage) o;
        return productIdsAndAmounts.equals(that.productIdsAndAmounts) && lastEdited.equals(that.lastEdited);
    }

    public OffsetDateTime getLastEdited() {
        return lastEdited;
    }

    public List<Pair<Integer, Integer>> getProductIds() {
        return productIdsAndAmounts;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productIdsAndAmounts, lastEdited);
    }
}
