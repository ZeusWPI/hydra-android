package be.ugent.zeus.hydra.urgent;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static be.ugent.zeus.hydra.urgent.MediaState.*;

/**
 * Google hates enums, so let's use ints.
 *
 * @author Niko Strijbol
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({IDLE, INITIALIZED, PREPARING, PREPARED, STARTED, STOPPED, PAUSED, COMPLETED, END, ERROR})
public @interface MediaState {
    int IDLE = 0;
    int INITIALIZED = 1;
    int PREPARING = 2;
    int PREPARED = 3;
    int STARTED = 4;
    int STOPPED = 5;
    int PAUSED = 6;
    int COMPLETED = 7;
    int END = 8;
    int ERROR = 9;
}