package be.ugent.zeus.hydra.common;

import android.support.annotation.NonNull;
import android.util.SparseArray;

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
     *
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