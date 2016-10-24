package be.ugent.zeus.hydra.urgent.track;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Class to manage the tracks. The user is responsible to synchronise the current track of the manager with the actual
 * playing, by using the methods {@link #playNext()} and {@link #playPrevious()}.
 *
 * @author Niko Strijbol
 */
public class TrackManager {

    private static final String TAG = "TrackManager";

    private final ArrayList<Track> trackQueue = new ArrayList<>();
    private int currentTrack = 0;

    /**
     * @return True if there is a previous track.
     */
    public boolean hasNext() {
        return trackQueue.size() > 0 && currentTrack < trackQueue.size() - 1;
    }

    /**
     * @return True if there is a next track.
     */
    public boolean hasPrevious() {
        return trackQueue.size() > 0 && currentTrack > 0;
    }

    /**
     * Add a track.
     *
     * @param track The track.
     */
    public void addTrack(Track track) {
        trackQueue.add(track);
        Log.d(TAG, "Added track: " + track.getTitle());
    }

    /**
     * Add multiple tracks.
     *
     * @param tracks The tracks.
     *
     * @exception IOException If the track was not addable.
     */
    public void addTracks(Collection<Track> tracks) throws IOException {
        trackQueue.addAll(tracks);
        Log.d(TAG, "Added " + tracks.size() + " tracks.");
    }

    /**
     * Remove all tracks. This sets the current track to nothing.
     */
    public void clear() {
        trackQueue.clear();
        currentTrack = 0;
        Log.d(TAG, "Cleared tags.");
    }

    /**
     * Remove all tracks and trim the internal array size back to zero. This will also remove the listener.
     */
    public void releaseTracks() {
        clear();
        trackQueue.trimToSize();
        Log.d(TAG, "Released tags.");
    }

    /**
     * @return True if there are any tracks available.
     */
    public boolean hasTracks() {
        return !trackQueue.isEmpty();
    }

    /**
     * @return The track marked as being played.
     */
    public Track currentTrack() {
        return trackQueue.get(0);
    }

    /**
     * Call this function when the next track is playing.
     */
    public void playNext() {
        currentTrack++;
    }

    /**
     * Call this function when the previous track is playing.
     */
    public void playPrevious() {
        currentTrack--;
    }

    /**
     * Help method to get the next track and mark it as current.
     *
     * @return The next track that is now playing.
     */
    public Track next() {
        playNext();
        return currentTrack();
    }

    /**
     * Help method to get the previous track and mark it as current.
     *
     * @return The previous track that is now playing.
     */
    public Track previous() {
        playPrevious();
        return currentTrack();
    }
}