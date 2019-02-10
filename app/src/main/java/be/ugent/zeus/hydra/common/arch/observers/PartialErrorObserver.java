package be.ugent.zeus.hydra.common.arch.observers;

import androidx.lifecycle.Observer;
import androidx.annotation.Nullable;

import be.ugent.zeus.hydra.common.request.RequestException;
import be.ugent.zeus.hydra.common.request.Result;
import java9.util.function.Consumer;

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