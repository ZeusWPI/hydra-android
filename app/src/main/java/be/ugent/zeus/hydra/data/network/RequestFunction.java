package be.ugent.zeus.hydra.data.network;

import be.ugent.zeus.hydra.data.network.exceptions.PartialDataException;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;

/**
 * Equivalent to {@link java8.util.function.Function}, but allows for an exception.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface RequestFunction<T, R> {

    R apply(T t) throws RequestFailureException, PartialDataException;
}