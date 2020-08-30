package be.ugent.zeus.hydra.common.arch.observers;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import java.util.function.Consumer;

import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * Calls the listener if the result has no data and only an exception.
 *
 * This differs from {@link PartialErrorObserver}, which will always call the listener if there is an exception, even
 * if there is data also.
 *
 * @author Niko Strijbol
 */
public abstract class ErrorObserver<D> implements Observer<Result<D>> {

    public static <D> ErrorObserver<D> with(Consumer<RequestException> consumer) {
        return new ErrorObserver<D>() {
            @Override
            protected void onError(RequestException throwable) {
                consumer.accept(throwable);
            }
        };
    }

    @Override
    public void onChanged(@Nullable Result<D> e) {
        if (e != null && !e.hasData()) {
            onError(e.getError());
        }
    }

    protected abstract void onError(RequestException throwable);
}