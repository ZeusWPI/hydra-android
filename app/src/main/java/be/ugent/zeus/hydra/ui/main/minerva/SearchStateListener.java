package be.ugent.zeus.hydra.ui.main.minerva;

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