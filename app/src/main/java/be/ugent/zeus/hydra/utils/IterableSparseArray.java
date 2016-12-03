package be.ugent.zeus.hydra.utils;

import android.util.Pair;
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

    /**
     * @return An iterator over the key/values of this array.
     */
    public Iterable<Pair<Integer, E>> pairIterable() {
        return () -> new Iterator<Pair<Integer, E>>() {

            private int current;

            @Override
            public boolean hasNext() {
                return size() > current;
            }

            @Override
            public Pair<Integer, E> next() {
                Pair<Integer, E> pair = new Pair<>(keyAt(current), valueAt(current));
                current++;
                return pair;
            }
        };
    }
}
