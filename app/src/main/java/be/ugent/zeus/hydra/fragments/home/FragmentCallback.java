package be.ugent.zeus.hydra.fragments.home;

/**
 * Interface for the connection between {@link HomeFragment} and {@link CacheHomeLoaderCallback}.
 *
 * @author Niko Strijbol
 */
interface FragmentCallback {

    /**
     * Called when the loading was successfully completed.
     */
    void onCompleted();

    /**
     * Called when an error occurred during the loading.
     *
     * @param errorMessage The error message. Use 0 for no error message.
     */
    void onError(String errorMessage);

    boolean shouldRefresh();
}