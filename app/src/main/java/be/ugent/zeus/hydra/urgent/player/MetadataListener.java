package be.ugent.zeus.hydra.urgent.player;

import android.support.annotation.Nullable;
import android.support.v4.media.MediaMetadataCompat;

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