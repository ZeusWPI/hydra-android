package be.ugent.zeus.hydra.utils;

import android.util.SparseArray;

import java.util.Iterator;

/**
 * A {@link SparseArray} that is iterable.
 *
 * @author Niko Strijbol
 */
public class IterableSparseArray<E> extends SparseArray<E> implements Iterable<E> {

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