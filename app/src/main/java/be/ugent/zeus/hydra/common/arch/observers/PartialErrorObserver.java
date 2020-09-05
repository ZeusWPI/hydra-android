package be.ugent.zeus.hydra.common.arch.observers;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import java.util.function.Consumer;

import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;

/**
 * Calls the listener when the result has an exception, even if it has data as well.
 *
 * @author Niko Strijbol
 */
public abstract class PartialErrorObserver<D> implements Observer<Result<D>> {

    public static <D> PartialErrorObserver<D> with(Consumer<RequestException> consumer) {
        return new PartialErrorObserver<D>() {
            @Override
            protected void onPartialError(RequestException throwable) {
                consumer.accept(throwable);
            }
        };
    }

    @Override
    public void onChanged(@Nullable Result<D> e) {
        if (e != null && e.hasException()) {
            onPartialError(e.getError());
        }
    }

    protected abstract void onPartialError(RequestException throwable);
}