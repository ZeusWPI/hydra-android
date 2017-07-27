package be.ugent.zeus.hydra.utils;

import android.util.SparseArray;

import java.util.Iterator;

/**
 * A {@link SparseArray} that is iterable.
 *
 * @author Niko Strijbol
 */
public class IterableSparseArray<E> extends SparseArray<E> implements Iterable<E> {

    /**
     * An iterator over the values of this array.
     *
     * The iterator is backed by this array; as such changes to the array while iterating may result in undefined
     * behaviour. The iterator is read-only: {@link Iterator#remove()} is not supported.
     *
     * @return The iterator.
     */
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
}