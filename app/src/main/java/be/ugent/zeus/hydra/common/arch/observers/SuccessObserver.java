package be.ugent.zeus.hydra.common.arch.observers;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import be.ugent.zeus.hydra.common.request.Result;
import java8.util.function.Consumer;

/**
 * Observes successful loading of data.
 *
 * @author Niko Strijbol
 */
public abstract class SuccessObserver<D> implements Observer<Result<D>> {

    @Override
    public void onChanged(@Nullable Result<D> result) {

        if (result == null) {
            onEmpty();
            return;
        }

        result.ifPresentOrElse(this::onSuccess, this::onEmpty);
    }

    /**
     * Called when there is no result, so nothing should be shown. The default method will do nothing.
     */
    protected void onEmpty() {}

    /**
     * Called when the result was successful and there is data.
     *
     * @param data The data.
     */
    protected abstract void onSuccess(D data);

    public static <D> SuccessObserver<D> with(Consumer<D> onSuccess) {
        return new SuccessObserver<D>() {
            @Override
            protected void onSuccess(D data) {
                onSuccess.accept(data);
            }
        };
    }
}