package be.ugent.zeus.hydra.common.arch.observers;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import java8.util.function.Consumer;

/**
 * Calls the listener when the result has an exception, even if it has data as well.
 *
 * @author Niko Strijbol
 */
public abstract class PartialErrorObserver<D> implements Observer<Result<D>> {

    @Override
    public void onChanged(@Nullable Result<D> e) {
        if (e != null && e.hasException()) {
            onPartialError(e.getError());
        }
    }

    protected abstract void onPartialError(RequestException throwable);

    public static <D> PartialErrorObserver<D> with(Consumer<RequestException> consumer) {
        return new PartialErrorObserver<D>() {
            @Override
            protected void onPartialError(RequestException throwable) {
                consumer.accept(throwable);
            }
        };
    }
}