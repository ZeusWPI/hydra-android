package be.ugent.zeus.hydra.common.arch.observers;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import java8.util.function.Consumer;

/**
 * Calls the listener if the result has no data and only an exception.
 *
 * This differs from {@link PartialErrorObserver}, which will always call the listener if there is an exception, even
 * if there is data also.
 *
 * @author Niko Strijbol
 */
public abstract class ErrorObserver<D> implements Observer<Result<D>> {

    @Override
    public void onChanged(@Nullable Result<D> e) {
        if (e != null && !e.hasData()) {
            onError(e.getError());
        }
    }

    protected abstract void onError(RequestException throwable);

    public static <D> ErrorObserver<D> with(Consumer<RequestException> consumer) {
        return new ErrorObserver<D>() {
            @Override
            protected void onError(RequestException throwable) {
                consumer.accept(throwable);
            }
        };
    }
}