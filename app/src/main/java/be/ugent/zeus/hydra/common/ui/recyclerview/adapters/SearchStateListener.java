package be.ugent.zeus.hydra.common.ui.recyclerview.adapters;

/**
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface SearchStateListener {

    /**
     * Called when dragging becomes enabled or disabled.
     *
     * @param isSearching True if dragging is enabled, false otherwise.
     */
    void onSearchStateChange(boolean isSearching);
}