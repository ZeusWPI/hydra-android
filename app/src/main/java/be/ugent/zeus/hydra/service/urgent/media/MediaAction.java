package be.ugent.zeus.hydra.service.urgent.media;

import android.util.Log;

import be.ugent.zeus.hydra.service.urgent.MusicService;

/**
 * Media button actions.
 *
 * @author Niko Strijbol
 */
@SuppressWarnings("WeakerAccess")
public final class MediaAction {

    private static final String TAG = "MediaAction";

    //No instances.
    private MediaAction() {}

    public final static String PLAY = "action_play";
    public final static String PAUSE = "action_pause";
    public final static String REWIND = "action_rewind";
    public final static String FAST_FORWARD = "action_fast_forward";
    public final static String NEXT = "action_next";
    public final static String PREVIOUS = "action_previous";
    public final static String STOP = "action_stop";
    public final static String FINISH = "action_finish";

    /**
     * Do the action.
     * @param action The action.
     * @param service The service.
     */
    public static void doAction(String action, MusicService service) {
        switch (action) {
            case MediaAction.PLAY:
                service.play();
                return;
            case MediaAction.PAUSE:
                service.pause();
                return;
            case MediaAction.FAST_FORWARD:
                service.fastForward();
                return;
            case MediaAction.REWIND:
                service.rewind();
                return;
            case MediaAction.PREVIOUS:
                service.previous();
                return;
            case MediaAction.NEXT:
                service.next();
                return;
            case MediaAction.STOP:
                service.stop();
                return;
            case MediaAction.FINISH:
                service.finish();
                return;
            default:
                Log.e(TAG, "Wrong media action.");
        }
    }
}