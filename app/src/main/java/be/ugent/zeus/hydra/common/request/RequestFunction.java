package be.ugent.zeus.hydra.common.request;

/**
 * Equivalent to {@link java8.util.function.Function}, but allows for an exception.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface RequestFunction<T, R> {

    R apply(T t) throws RequestException;
}