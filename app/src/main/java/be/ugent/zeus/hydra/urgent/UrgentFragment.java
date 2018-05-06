package be.ugent.zeus.hydra.urgent;

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
import be.ugent.zeus.hydra.urgent.player.MusicService;
import be.ugent.zeus.hydra.utils.NetworkUtils;

import java.util.List;

/**
 * Fragment that displays the Urgent.fm player, meaning controls and track information.
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
                    Toast.makeText(requireContext(), "Errror", Toast.LENGTH_LONG).show();
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
                    MediaControllerCompat mediaController = MediaControllerCompat
                            .getMediaController(requireActivity());
                    if (mediaController != null) {
                        mediaController.unregisterCallback(mediaControllerCallback);
                        MediaControllerCompat.setMediaController(requireActivity(), null);
                    }
                }
            };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_urgent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        albumImage = view.findViewById(R.id.albumImage);
        artistText = view.findViewById(R.id.artistText);
        titleText = view.findViewById(R.id.titleText);
        progressBar = view.findViewById(R.id.progress_bar);
        playPauseButton = view.findViewById(R.id.playPauseButton);

        // Attach links to social media buttons.
        view.findViewById(R.id.social_facebook)
                .setOnClickListener(v -> NetworkUtils.maybeLaunchBrowser(getContext(), FACEBOOK_URL));
        view.findViewById(R.id.social_youtube)
                .setOnClickListener(v -> NetworkUtils.maybeLaunchBrowser(getContext(), YOUTUBE_URL));
        view.findViewById(R.id.social_instagram)
                .setOnClickListener(v -> NetworkUtils.maybeLaunchBrowser(getContext(), INSTAGRAM_URL));
        view.findViewById(R.id.social_urgentfm)
                .setOnClickListener(v -> NetworkUtils.maybeLaunchBrowser(getContext(), URGENT_URL));

        mediaBrowser = new MediaBrowserCompat(requireActivity(), new ComponentName(requireActivity(), MusicService.class), connectionCallback, null);
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