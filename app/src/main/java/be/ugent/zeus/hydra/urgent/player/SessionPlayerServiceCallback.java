package be.ugent.zeus.hydra.urgent.player;

import android.support.v4.media.session.PlaybackStateCompat;

/**
 * An interface to meant to link the SessionCallbacks to the interface.
 *
 * @author Niko Strijbol
 */
public interface SessionPlayerServiceCallback {

    /**
     * Called when the state of the session changes.
     */
    void onSessionStateChanged(@PlaybackStateCompat.State int newState);
}