package be.ugent.zeus.hydra.repository.utils;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import java8.util.function.Predicate;

/**
 * This class contains some generic utility methods to use on {@link LiveData}.
 *
 * @author Niko Strijbol
 */
public class UtilTransformations {

    /**
     * Applies a filter to a live data. The resulting live data will only emit events when the {@code filter} returns
     * true.
     *
     * @param source The source of events.
     * @param filter The filter to apply to the events.
     * @param <D>    The type of data.
     *
     * @return A live data that will only emit the events.
     */
    @MainThread
    public static <D> LiveData<D> filter(LiveData<D> source, Predicate<D> filter) {
        final MediatorLiveData<D> result = new MediatorLiveData<>();
        result.addSource(source, x -> {
            if (filter.test(x)) {
                result.setValue(x);
            }
        });
        return result;
    }
}