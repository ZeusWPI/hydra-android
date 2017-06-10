package be.ugent.zeus.hydra.repository.observers;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import be.ugent.zeus.hydra.data.network.exceptions.RequestException;
import be.ugent.zeus.hydra.repository.Result;

import java.util.function.Consumer;

/**
 * @author Niko Strijbol
 */
public abstract class ErrorObserver<D> implements Observer<Result<D>> {

    @Override
    public void onChanged(@Nullable Result<D> e) {
        if (e != null && e.hasException()) {
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
