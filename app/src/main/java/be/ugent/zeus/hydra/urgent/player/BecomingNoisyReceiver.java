package be.ugent.zeus.hydra.urgent.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;

/**
 * Helper class for listening for when headphones are unplugged (or the audio
 * will otherwise cause playback to become "noisy").
 */
class BecomingNoisyReceiver extends BroadcastReceiver {

    private final Context context;
    private final IntentFilter noisyIntentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private final MediaControllerCompat controller;

    private boolean registered = false;

    BecomingNoisyReceiver(Context context, MediaSessionCompat mediaSession) {
        this.context = context;
        this.controller = new MediaControllerCompat(context, mediaSession);
    }

    void register() {
        if (!registered) {
            context.registerReceiver(this, noisyIntentFilter);
            registered = true;
        }
    }

    void unregister() {
        if (registered) {
            context.unregisterReceiver(this);
            registered = false;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            controller.getTransportControls().pause();
        }
    }
}