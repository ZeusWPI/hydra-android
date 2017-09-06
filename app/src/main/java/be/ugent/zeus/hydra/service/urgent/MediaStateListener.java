package be.ugent.zeus.hydra.service.urgent;

/**
 * Listen to changes of the state of the {@link Playback}.
 *
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface MediaStateListener {

    /**
     * Called when the state changes.
     *
     * @param oldState The previous state.
     * @param newState The current state.
     */
    void onStateChanged(@MediaState int oldState, @MediaState int newState);
}