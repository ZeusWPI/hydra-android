package be.ugent.zeus.hydra.common.arch.data;

import java.util.Optional;

/**
 * An event represents an one-off user action.
 *
 * Typical use-cases are showing snack bars, toasts, etc.
 *
 * Classes that listen to this will probably want to use the {@link be.ugent.zeus.hydra.common.arch.observers.EventObserver}.
 *
 * @author Niko Strijbol
 * @see <a href="https://goo.gl/JH3KxR">Medium article</a>
 */
public class Event<D> {

    private final D data;
    private boolean handled;

    public Event(D data) {
        this.data = data;
    }

    /**
     * Get the data if the data has not been handled yet. If the data has been handled, the optional will be empty.
     * If data has not been handled yet, it will now be considered handled.
     *
     * @return The data or not.
     */
    public Optional<D> handleData() {
        if (handled) {
            return Optional.empty();
        } else {
            handled = true;
            return Optional.ofNullable(data);
        }
    }
}
