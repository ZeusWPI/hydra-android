package be.ugent.zeus.hydra.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Niko Strijbol
 */
public class SetUtils {

    /**
     * @param set1 Set 1.
     * @param set2 Set 2.
     * @param <E> The type.
     * @return A new set containing all elements from set1 and set2.
     */
    public static <E> Set<E> union(final Set<E> set1, final Set<E> set2) {
        Set<E> union = new HashSet<>(set1);
        union.addAll(set2);
        return union;
    }

    /**
     * @param set1 Set 1.
     * @param set2 Set 2.
     * @param <E> The type.
     * @return A new set containing only the elements present in both set1 and set2.
     */
    public static <E> Set<E> intersect(final Set<E> set1, final Set<E> set2) {
        Set<E> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection;
    }

    /**
     * @param set1 Set 1.
     * @param set2 Set 2.
     * @param <E> The type.
     * @return A new set containing the elements in set1 that are not in set2.
     */
    public static <E> Set<E> difference(final Set<E> set1, final Set<E> set2) {
        Set<E> difference = new HashSet<>(set1);
        difference.removeAll(set2);
        return difference;
    }
}