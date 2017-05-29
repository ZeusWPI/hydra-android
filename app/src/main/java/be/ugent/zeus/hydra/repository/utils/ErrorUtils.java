package be.ugent.zeus.hydra.repository.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import be.ugent.zeus.hydra.repository.Result;
import java8.util.function.Consumer;

/**
 * Enables using a {@link Consumer} as error callback.
 *
 * @author Niko Strijbol
 */
public class ErrorUtils {

    /**
     * Make a live data that only returns errors.
     *
     * @param source The source of the events.
     * @param <D> The data.
     *
     * @return The new live data.
     */
    public static <D> LiveData<Throwable> filterErrors(LiveData<Result<D>> source) {
        return Transformations.map(
                UtilTransformations.filter(source, dResult -> dResult.getStatus() == Result.Status.ERROR),
                Result::getError
        );
    }
}