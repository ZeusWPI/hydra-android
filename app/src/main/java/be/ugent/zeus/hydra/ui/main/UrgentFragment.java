package be.ugent.zeus.hydra.ui.main;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.service.urgent.MusicService;

import java.util.List;

/**
 * @author Niko Strijbol
 */
public class UrgentFragment extends Fragment {

    private static final String TAG = "UrgentFragment";

    @DrawableRes
    private static final int PLAY_DRAWABLE = R.drawable.ic_play_arrow_24dp;
    @DrawableRes
    private static final int PAUSE_DRAWABLE = R.drawable.ic_stop;

    private ImageButton playPauseButton;
    private TextView artistText;
    private TextView titleText;
    private ImageView albumImage;
    private View progressBar;

    private MediaBrowserCompat mediaBrowser;
    private boolean shouldUpdateButton = false;

    // Receive callbacks from the MediaController. Here we update our state such as which queue
    // is being shown, the current title and description and the PlaybackState.
    private final MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            Log.d(TAG, "onPlaybackStateChanged: state is " + state.toString());
            configureButtons();
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata == null) {
                return;
            }
            Log.d(TAG, "Received metadata state change to mediaId=" +
                    metadata.getDescription().getMediaId() +
                    " song=" + metadata.getDescription().getTitle());
            readMetadata(metadata);
        }
    };

    private MediaBrowserCompat.SubscriptionCallback subscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    initMediaControls();
                    configureButtons();
                }

                @Override
                public void onError(@NonNull String id) {
                    Toast.makeText(getActivity(), "Errror", Toast.LENGTH_LONG).show();
                }
            };

    private MediaBrowserCompat.ConnectionCallback connectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    Log.d(TAG, "onConnected: session token " + mediaBrowser.getSessionToken());

                    mediaBrowser.subscribe(mediaBrowser.getRoot(), subscriptionCallback);
                    try {
                        MediaControllerCompat mediaController =
                                new MediaControllerCompat(getActivity(), mediaBrowser.getSessionToken());
                        MediaControllerCompat.setMediaController(getActivity(), mediaController);

                        // Register a Callback to stay in sync
                        mediaController.registerCallback(mediaControllerCallback);
                    } catch (RemoteException e) {
                        Log.e(TAG, "Failed to connect to MediaController", e);
                    }
                }

                @Override
                public void onConnectionFailed() {
                    Log.e(TAG, "onConnectionFailed");
                }

                @Override
                public void onConnectionSuspended() {
                    Log.d(TAG, "onConnectionSuspended");
                    MediaControllerCompat mediaController = MediaControllerCompat
                            .getMediaController(getActivity());
                    if (mediaController != null) {
                        mediaController.unregisterCallback(mediaControllerCallback);
                        MediaControllerCompat.setMediaController(getActivity(), null);
                    }
                }
            };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_urgent, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        albumImage = view.findViewById(R.id.albumImage);
        artistText = view.findViewById(R.id.artistText);
        titleText = view.findViewById(R.id.titleText);
        progressBar = view.findViewById(R.id.progress_bar);
        playPauseButton = view.findViewById(R.id.playPauseButton);

        mediaBrowser = new MediaBrowserCompat(getActivity(),
                new ComponentName(getActivity(), MusicService.class), connectionCallback, null);
        hideMediaControls();
    }

    @Override
    public void onStart() {
        super.onStart();
        mediaBrowser.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaBrowser.disconnect();
    }

    /**
     * Init the media control fragment.
     */
    private void initMediaControls() {
        playPauseButton.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void hideMediaControls() {
        playPauseButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void readMetadata(MediaMetadataCompat metadata) {
        if (metadata != null) {
            MediaDescriptionCompat descriptionCompat = metadata.getDescription();
            //These must be set at least.
            artistText.setText(descriptionCompat.getSubtitle());
            titleText.setText(descriptionCompat.getTitle());

            //Try setting the album art.
            if (metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART) != null) {
                albumImage.setImageBitmap(descriptionCompat.getIconBitmap());
            } else {
                albumImage.setImageResource(R.drawable.ic_album);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (shouldUpdateButton) {
            configureButtons();
            shouldUpdateButton = false;
        }
    }

    @SuppressLint("SwitchIntDef")
    private void configureButtons() {

        if (isDetached() || getActivity() == null) {
            shouldUpdateButton = true;
            return;
        }

        MediaControllerCompat mediaController = MediaControllerCompat.getMediaController(getActivity());
        PlaybackStateCompat state = mediaController.getPlaybackState();

        boolean enablePlay = false;
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                enablePlay = true;
                break;
            case PlaybackStateCompat.STATE_ERROR:
                Log.e(TAG, "error playbackstate: " + state.getErrorMessage());
                Toast.makeText(getActivity(), state.getErrorMessage(), Toast.LENGTH_LONG).show();
                break;
            default:
                enablePlay = true;
        }

        // Show the progress bar to show the user we are actually doing something.
        if (state.getState() == PlaybackStateCompat.STATE_BUFFERING || state.getState() == PlaybackStateCompat.STATE_CONNECTING) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }

        if (enablePlay) {
            playPauseButton.setImageResource(PLAY_DRAWABLE);
        } else {
            playPauseButton.setImageResource(PAUSE_DRAWABLE);
        }

        playPauseButton.setOnClickListener(v -> {
            if (state.getState() == PlaybackStateCompat.STATE_PLAYING) {
                mediaController.getTransportControls().stop();
            } else {
                mediaController.getTransportControls().play();
            }
        });
    }
}