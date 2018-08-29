package be.ugent.zeus.hydra.urgent.player;

/**
 * An interface to meant to link the SessionCallbacks to the interface.
 *
 * @author Niko Strijbol
 */
public interface PlayerSessionServiceCallback {

    /**
     * Called when playback should start.
     */
    void onPlay();

    /**
     * Called when playback is temporarily stopped.
     */
    void onPause();

    /**
     * Called when playback is (more) permanently stopped.
     */
    void onStop();
}
