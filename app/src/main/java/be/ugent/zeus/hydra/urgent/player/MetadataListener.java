package be.ugent.zeus.hydra.urgent.player;

import android.support.v4.media.MediaMetadataCompat;
import androidx.annotation.Nullable;

/**
 * @author Niko Strijbol
 */
@FunctionalInterface
public interface MetadataListener {
    /**
     * Called when the metadata is updated.
     *
     * @param metadataCompat The updated meta data.
     */
    void onMetadataUpdate(@Nullable MediaMetadataCompat metadataCompat);
}