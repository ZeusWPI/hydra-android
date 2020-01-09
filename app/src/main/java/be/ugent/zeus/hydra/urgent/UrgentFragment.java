package be.ugent.zeus.hydra.urgent;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.urgent.player.UrgentTrackProvider;
import be.ugent.zeus.hydra.utils.NetworkUtils;

/**
 * Fragment that displays the Urgent.fm player, meaning controls and track information. Note that while we implement
 * the streaming in {@link MusicService} and others, we don't interact with it directly. Instead, we control it via the
 * media session.
 *
 * @author Niko Strijbol
 */
public class UrgentFragment extends Fragment {

    private static final String TAG = "UrgentFragment";

    private static final String FACEBOOK_URL = "https://www.facebook.com/urgent.fm";
    private static final String YOUTUBE_URL = "https://www.youtube.com/channel/UCZgOQzaJUeIlvS5R7pqFsqQ";
    private static final String URGENT_URL = "http://www.urgent.fm/";
    private static final String INSTAGRAM_URL = "https://www.instagram.com/urgent.fm/";

    @DrawableRes
    private static final int PLAY_DRAWABLE = R.drawable.ic_play_arrow_24dp;
    @DrawableRes
    private static final int PAUSE_DRAWABLE = R.drawable.ic_stop;

    private ImageButton playPauseButton;
    private TextView artistText;
    private TextView titleText;
    private TextView descriptionText;
    private TextView descriptionTitle;
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

    private final MediaBrowserCompat.SubscriptionCallback subscriptionCallback =
            new MediaBrowserCompat.SubscriptionCallback() {
                @Override
                public void onChildrenLoaded(@NonNull String parentId, @NonNull List<MediaBrowserCompat.MediaItem> children) {
                    initMediaControls();
                    configureButtons();
                }

                @Override
                public void onError(@NonNull String id) {
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show();
                }
            };

    private final MediaBrowserCompat.ConnectionCallback connectionCallback =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    mediaBrowser.subscribe(mediaBrowser.getRoot(), subscriptionCallback);
                    try {
                        MediaControllerCompat mediaController =
                                new MediaControllerCompat(requireActivity(), mediaBrowser.getSessionToken());
                        MediaControllerCompat.setMediaController(requireActivity(), mediaController);

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
                    disconnect();
                }
            };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_urgent, container, false);
    }

    private void disconnect() {
        // If we are not connected yet, we don't need to unsubscribe.
        if (mediaBrowser.isConnected()) {
            mediaBrowser.unsubscribe(mediaBrowser.getRoot(), subscriptionCallback);
        }
        MediaControllerCompat mediaController = MediaControllerCompat
                .getMediaController(requireActivity());
        if (mediaController != null) {
            mediaController.unregisterCallback(mediaControllerCallback);
            MediaControllerCompat.setMediaController(requireActivity(), null);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        albumImage = view.findViewById(R.id.albumImage);
        artistText = view.findViewById(R.id.artistText);
        titleText = view.findViewById(R.id.titleText);
        progressBar = view.findViewById(R.id.progress_bar);
        playPauseButton = view.findViewById(R.id.playPauseButton);
        descriptionText = view.findViewById(R.id.programme_description);
        descriptionTitle = view.findViewById(R.id.description_title);

        // Attach links to social media buttons.
        view.findViewById(R.id.social_facebook)
                .setOnClickListener(v -> NetworkUtils.maybeLaunchBrowser(getContext(), FACEBOOK_URL));
        view.findViewById(R.id.social_youtube)
                .setOnClickListener(v -> NetworkUtils.maybeLaunchBrowser(getContext(), YOUTUBE_URL));
        view.findViewById(R.id.social_instagram)
                .setOnClickListener(v -> NetworkUtils.maybeLaunchBrowser(getContext(), INSTAGRAM_URL));
        view.findViewById(R.id.social_urgentfm)
                .setOnClickListener(v -> NetworkUtils.maybeLaunchBrowser(getContext(), URGENT_URL));

        mediaBrowser = new MediaBrowserCompat(requireActivity(),
                new ComponentName(requireActivity(), MusicService.class), connectionCallback, null);
        hideMediaControls();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: connecting to media browser");
        if (mediaBrowser.isConnected()) {
            Log.w(TAG, "onStart: already connected, doing nothing.");
        } else {
            mediaBrowser.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: disconnecting media browser");
        if (mediaBrowser.isConnected()) {
            disconnect();
            mediaBrowser.disconnect();
        } else {
            Log.w(TAG, "onStop: not connected, doing nothing.");
        }
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requireActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @SuppressLint("WrongConstant")
    private void readMetadata(MediaMetadataCompat metadata) {
        if (metadata != null && getContext() != null) {
            MediaDescriptionCompat descriptionCompat = metadata.getDescription();
            // These must be set at least.
            artistText.setText(descriptionCompat.getSubtitle());
            titleText.setText(descriptionCompat.getTitle());

            // Set album URI (or attempt to).
            if (descriptionCompat.getIconBitmap() != null) {
                albumImage.setImageBitmap(descriptionCompat.getIconBitmap());
            } else if (descriptionCompat.getIconUri() != null) {
                albumImage.setImageURI(descriptionCompat.getIconUri());
            } else {
                albumImage.setImageResource(R.drawable.ic_album);
            }

            if (!TextUtils.isEmpty(metadata.getString(UrgentTrackProvider.METADATA_DESCRIPTION))) {
                descriptionText.setVisibility(View.VISIBLE);
                descriptionText.setText(metadata.getString(UrgentTrackProvider.METADATA_DESCRIPTION).trim());
                descriptionTitle.setVisibility(View.VISIBLE);
                descriptionTitle.setText(getString(R.string.urgent_about_programme, descriptionCompat.getTitle()));
            } else {
                descriptionText.setVisibility(View.GONE);
                descriptionTitle.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
            case PlaybackStateCompat.STATE_ERROR:
                Toast.makeText(getActivity(), R.string.urgent_error, Toast.LENGTH_SHORT).show();
                break;
            case PlaybackStateCompat.STATE_PLAYING:
            case PlaybackStateCompat.STATE_CONNECTING:
            case PlaybackStateCompat.STATE_BUFFERING:
                break; // Do nothing.
            case PlaybackStateCompat.STATE_PAUSED:
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
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING:
                    mediaController.getTransportControls().pause();
                    break;
                case PlaybackStateCompat.STATE_ERROR:
                case PlaybackStateCompat.STATE_CONNECTING:
                case PlaybackStateCompat.STATE_BUFFERING:
                    mediaController.getTransportControls().stop();
                    break;
                default:
                    mediaController.getTransportControls().play();
            }
        });
    }
}
