package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

import androidx.annotation.NonNull;

/**
 * @author Niko Strijbol
 */
public interface SearchHelper {

    /**
     * @return True if searching is active, otherwise false.
     */
    boolean isSearching();

    /**
     * Register a listener for the search state. If the listener is already registered, the behaviour is safe, but
     * undefined.
     *
     * The registered listeners are kept with a weak reference; meaning that they will be garbage collected if nothing
     * else references them. Therefor, calling {@link #unregisterSearchListener(SearchStateListener)} is not always needed.
     *
     * @param listener The listener.
     */
    void registerSearchListener(@NonNull SearchStateListener listener);

    /**
     * Remove a listener. If the listener was registered multiple times, the behaviour is safe, but undefined. If the
     * listener was not registered, this does nothing.
     *
     * @param listener The listener.
     */
    void unregisterSearchListener(@NonNull SearchStateListener listener);
}