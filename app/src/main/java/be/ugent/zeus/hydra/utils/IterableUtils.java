package be.ugent.zeus.hydra.utils;

import java8.util.function.Function;
import java8.util.stream.Collectors;
import java8.util.stream.StreamSupport;

import java.util.Collection;
import java.util.List;

/**
 * @author Niko Strijbol
 */
public class IterableUtils {

    public static <I,O> List<O> transform(Collection<I> in, Function<I, O> mapper) {
        return StreamSupport.stream(in)
                .map(mapper)
                .collect(Collectors.toList());
    }

}
