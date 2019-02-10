package be.ugent.zeus.hydra.urgent.player;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;

import org.threeten.bp.Instant;
import org.threeten.bp.temporal.ChronoUnit;

/**
 * Connects a {@link Player} to a {@link MediaSessionCompat} with the callback, in one direction: it passes commands
 * from the media session to the player.
 *
 * @author Niko Strijbol
 *
 * @see SessionPlayerCallback for the reverse mapping.
 */
class PlayerSessionCallback extends MediaSessionCompat.Callback {

    private static final String TAG = "PlayerSessionCallback";

    private final Player player;
    private final BecomingNoisyReceiver receiver;
    private final PlayerSessionServiceCallback serviceCallback;
    private final Handler handler = new Handler();

    private Runnable nextUpdate;

    PlayerSessionCallback(@NonNull Player player, @NonNull BecomingNoisyReceiver receiver, @NonNull PlayerSessionServiceCallback serviceCallback) {
        this.player = player;
        this.serviceCallback = serviceCallback;
        this.receiver = receiver;
    }

    @Override
    public void onPlay() {
        super.onPlay();
        Log.d(TAG, "onPlay called");
        player.setPlayWhenReady(true);
        serviceCallback.onPlay();
        receiver.register();
        scheduleMetadataUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        receiver.unregister();
        cancelMetadataUpdate();
        player.setPlayWhenReady(false);
        serviceCallback.onPause();
        player.destroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop called");
        receiver.unregister();
        cancelMetadataUpdate();
        serviceCallback.onPause();
        player.destroy();
        serviceCallback.onStop();
    }

    @Override
    public void onPlayFromMediaId(String mediaId, Bundle extras) {
        if (UrgentTrackProvider.URGENT_ID.equals(mediaId)) {
            onPlay();
        }
    }

    private void scheduleMetadataUpdate() {
        Log.d(TAG, "scheduleMetadataUpdate");
        cancelMetadataUpdate();
        nextUpdate = getMetadataUpdate();

        Instant time = Instant.now().plus(1, ChronoUnit.HOURS)
                .truncatedTo(ChronoUnit.HOURS)
                .plus(1, ChronoUnit.MINUTES);

        long millis = Instant.now().until(time, ChronoUnit.MILLIS);
        Log.d(TAG, "scheduleMetadataUpdate: scheduling update in " + millis + " millis.");
        handler.postDelayed(nextUpdate, millis);
    }

    private void cancelMetadataUpdate() {
        if (nextUpdate != null) {
            Log.d(TAG, "cancelMetadataUpdate");
            handler.removeCallbacks(nextUpdate);
            nextUpdate = null;
        }
    }

    private Runnable getMetadataUpdate() {
        return () -> player.getProvider().prepareMedia(player::receiveTrackInformation);
    }
}
