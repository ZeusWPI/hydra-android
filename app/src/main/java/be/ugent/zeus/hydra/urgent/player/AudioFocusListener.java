package be.ugent.zeus.hydra.urgent.player;

import android.media.AudioManager;
import android.util.Log;

/**
 * @author Niko Strijbol
 */
class AudioFocusListener implements AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "AudioFocusListener";

    private final Player player;

    private boolean resumeOnFocusGain = false;

    AudioFocusListener(Player player) {
        this.player = player;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        Log.d(TAG, "onAudioFocusChange: " + focusChange);
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                player.setDefaultVolume();
                boolean shouldResume = resumeOnFocusGain;
                resumeOnFocusGain = false; // Prevent callback loops
                if (shouldResume) {
                    player.setPlayWhenReady(true);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                player.setDuckVolume();
                // Fall though to the next one
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                resumeOnFocusGain = player.shouldPlayWhenReady;
                // Fall through to the next one
            case AudioManager.AUDIOFOCUS_LOSS:
                player.setPlayWhenReady(false);
                break;
        }
    }
}