package be.ugent.zeus.hydra.urgent.player;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static be.ugent.zeus.hydra.urgent.player.MediaStateListener.State.*;

/**
 * Listen to changes of the state of the {@link Player}.
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
    void onStateChanged(@State int oldState, @State int newState);

    /**
     * Google hates enums, so let's use ints.
     *
     * @author Niko Strijbol
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, STOPPED, PAUSED, PLAYBACK_COMPLETED, END, ERROR})
    @interface State {
        int IDLE = 0;
        int INITIALIZED = 1;
        int PREPARING = 2;
        int PREPARED = 3;
        int STARTED = 4;
        int STOPPED = 5;
        int PAUSED = 6;
        int PLAYBACK_COMPLETED = 7;
        int END = 8;
        int ERROR = 9;
    }
}