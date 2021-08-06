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

package be.ugent.zeus.hydra.common;

import android.util.SparseArray;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A {@link SparseArray} that is iterable.
 *
 * @author Niko Strijbol
 */
public class ExtendedSparseArray<E> extends SparseArray<E> implements Iterable<E> {

    /**
     * An iterator over the values of this array.
     * <p>
     * The iterator is backed by this array; as such changes to the array while iterating may result in undefined
     * behaviour. The iterator is read-only: {@link Iterator#remove()} is not supported.
     *
     * @return The iterator.
     */
    @NonNull
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private int current;

            @Override
            public boolean hasNext() {
                return size() > current;
            }

            @Override
            public E next() {
                return valueAt(current++);
            }
        };
    }

    /**
     * Get the keys used by this array. Because the actual array is private, we must iterate to produce it.
     *
     * @return The list of keys used by this array.
     */
    public List<Integer> getKeys() {
        List<Integer> keys = new ArrayList<>(size());
        for (int i = 0; i < size(); i++) {
            keys.add(keyAt(i));
        }
        return keys;
    }
}