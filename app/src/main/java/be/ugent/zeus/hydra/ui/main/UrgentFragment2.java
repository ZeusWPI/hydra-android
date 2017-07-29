package be.ugent.zeus.hydra.ui.main;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
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
import be.ugent.zeus.hydra.service.urgent.MusicBinder;
import be.ugent.zeus.hydra.service.urgent.MusicService;

/**
 * @author Niko Strijbol
 */
public class UrgentFragment2 extends Fragment {

    private static final String TAG = "UrgentFragment2";

    private static final String STATE_TOKEN = "state_token";

    @DrawableRes
    private static final int PLAY_DRAWABLE = R.drawable.ic_play_arrow_24dp;
    @DrawableRes
    private static final int PAUSE_DRAWABLE = R.drawable.ic_pause_24dp;

    private boolean isBound = false;
    private ServiceConnection serviceConnection = new MusicConnection();
    private MusicService musicService;

    private ImageButton playPauseButton;
    private ImageButton stopButton;
    private View buttonWrapper;
    private TextView artistText;
    private TextView titleText;
    private ImageView albumImage;
    private MediaControllerCompat mediaController;
    private MediaSessionCompat.Token token;
    private View progressBar;
    private TextView warning;

    // Receive callbacks from the MediaController. Here we update our state such as which queue
    // is being shown, the current title and description and the PlaybackState.
    private final MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            UrgentFragment2.this.onPlaybackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata == null) {
                return;
            }
            Log.d(TAG, "Received metadata state change to mediaId=" +
                    metadata.getDescription().getMediaId() +
                    " song=" + metadata.getDescription().getTitle());
            UrgentFragment2.this.onMetadataChanged(metadata);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_urgent_2, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            token = savedInstanceState.getParcelable(STATE_TOKEN);
        }

        albumImage = view.findViewById(R.id.albumImage);
        artistText = view.findViewById(R.id.artistText);
        titleText = view.findViewById(R.id.titleText);
        progressBar = view.findViewById(R.id.progress_bar);
        playPauseButton = view.findViewById(R.id.playPauseButton);
        stopButton = view.findViewById(R.id.stopButton);
        buttonWrapper = view.findViewById(R.id.button_wrap);
        warning = view.findViewById(R.id.urgent_warning);

        if (token != null) {
            initialiseController();
        }
    }

    private void initialiseController() {
        try {
            mediaController = new MediaControllerCompat(getContext(), token);
            mediaController.registerCallback(mediaControllerCallback);
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }

        playPauseButton.setImageResource(PLAY_DRAWABLE);
        readMetadata(mediaController.getMetadata());
        configureButtons(mediaController.getPlaybackState());
    }

    @Override
    public void onDetach() {
        if (mediaController != null) {
            mediaController.unregisterCallback(mediaControllerCallback);
        }
        super.onDetach();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Don't do anything if the service is bound.
        if (!isBound) {
            hideMediaControls();
            bind();
        }
    }

    private void bind() {
        // Start the service, doesn't matter if it is already started.
        Intent intent = new Intent(getActivity(), MusicService.class);
        Log.d(TAG, "onStart: Starting service");
        getContext().startService(intent);

        // Request a bind with the service.
        Log.d(TAG, "onStart: Requesting bind");
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isBound) {
            getActivity().unbindService(serviceConnection);
            musicService = null;
            isBound = false;
        }
    }

    /**
     * Init the media control fragment.
     */
    private void initMediaControls(MediaSessionCompat.Token token) {
        this.token = token;
        initialiseController();
        buttonWrapper.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void hideMediaControls() {
        buttonWrapper.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_TOKEN, token);
        super.onSaveInstanceState(outState);
    }

    private void readMetadata(MediaMetadataCompat metadata) {
        if (metadata != null) {
            //These must be set at least.
            artistText.setText(metadata.getText(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST));
            titleText.setText(metadata.getText(MediaMetadataCompat.METADATA_KEY_TITLE));

            //Try setting the album art.
            if (metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART) != null) {
                albumImage.setImageBitmap(metadata.getBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART));
            } else {
                albumImage.setImageResource(R.drawable.ic_album);
            }
        }
    }

    private void onMetadataChanged(MediaMetadataCompat metadata) {
        Log.d(TAG, "onMetadataChanged ");

        if (getActivity() == null) {
            Log.w(TAG, "onMetadataChanged called when getActivity null, this should not happen if the callback was properly unregistered. Ignoring.");
            return;
        }

        if (metadata == null) {
            return;
        }

        readMetadata(metadata);
    }

    private void onPlaybackStateChanged(PlaybackStateCompat state) {
        if (getActivity() == null) {
            Log.w(TAG, "onPlaybackStateChanged called when getActivity null, this should not happen if the callback was properly unregistered. Ignoring.");
            return;
        }

        // If we have restarted the service, attempt to bind again.
        if (state.getState() == PlaybackStateCompat.STATE_PLAYING && !isBound) {
            bind();
        }

        configureButtons(state);
    }

    private int getPlaybackState() {
        return mediaController.getPlaybackState().getState();
    }

    @SuppressLint("SwitchIntDef")
    private void configureButtons(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        boolean enablePlay = false;
        boolean disableStop = false;
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_PLAYING:
                warning.setVisibility(View.GONE);
                break;
            case PlaybackStateCompat.STATE_PAUSED:
                warning.setVisibility(View.VISIBLE);
                enablePlay = true;
                disableStop = false;
                break;
            case PlaybackStateCompat.STATE_ERROR:
                Log.e(TAG, "error playbackstate: " + state.getErrorMessage());
                Toast.makeText(getActivity(), state.getErrorMessage(), Toast.LENGTH_LONG).show();
                break;
            default:
                warning.setVisibility(View.GONE);
                enablePlay = true;
                disableStop = true;
        }

        if (enablePlay) {
            playPauseButton.setImageResource(PLAY_DRAWABLE);
        } else {
            playPauseButton.setImageResource(PAUSE_DRAWABLE);
        }

        if (disableStop) {
            stopButton.setVisibility(View.GONE);
        } else {
            stopButton.setVisibility(View.VISIBLE);
        }

        playPauseButton.setOnClickListener(v -> {
            if (getPlaybackState() == PlaybackStateCompat.STATE_PLAYING) {
                mediaController.getTransportControls().pause();
            } else {
                mediaController.getTransportControls().play();
            }
        });

        stopButton.setOnClickListener(v -> mediaController.getTransportControls().stop());
    }

    /**
     * The service connection. This is used to facilitate communication between the service and use.
     */
    private class MusicConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder binder = (MusicBinder) service;
            musicService = binder.getService();
            musicService.setTokenConsumer(UrgentFragment2.this::initMediaControls);
            isBound = true;
            Log.d(TAG, "onServiceConnected: MusicService is bound.");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            Log.d(TAG, "onServiceConnected: MusicService is unbound.");
        }
    }
}