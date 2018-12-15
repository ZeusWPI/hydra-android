package be.ugent.zeus.hydra.common.arch.observers;

import android.arch.lifecycle.Observer;

import be.ugent.zeus.hydra.common.arch.data.Event;
import java9.util.function.Consumer;

/**
 * Observer that will only handle non-handled events.
 *
 * @author Niko Strijbol
 */
public abstract class EventObserver<D> implements Observer<Event<D>> {

    @Override
    public void onChanged(Event<D> e) {
        if (e != null) {
            e.handleData().ifPresent(this::onUnhandled);
        }
    }

    protected abstract void onUnhandled(D data);

    public static <D> EventObserver<D> with(Consumer<D> consumer) {
        return new EventObserver<D>() {
            @Override
            protected void onUnhandled(D data) {
                consumer.accept(data);
            }
        };
    }
}