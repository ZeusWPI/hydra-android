/*
 * Copyright 2016 Allan Pichardo
 * Copyright 2016 Niko Strijbol
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.ugent.zeus.hydra.fragments.urgent;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
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

import static be.ugent.zeus.hydra.utils.ViewUtils.$;

public class MediaControlFragment extends Fragment {

    private static final String TAG = "MediaControlFragment";
    private static final String STATE_TOKEN = "state_token";
    private static final String STATE_PLAY = "state_play";
    private static final String STATE_PAUSE = "state_pause";

    private ImageButton playPauseButton;
    private TextView artistText;
    private TextView titleText;
    private ImageView albumImage;
    private MediaControllerCompat mediaController;
    private MediaSessionCompat.Token token;

    @DrawableRes
    private int playDrawableResource;
    @DrawableRes
    private int pauseDrawableResource;

    // Receive callbacks from the MediaController. Here we update our state such as which queue
    // is being shown, the current title and description and the PlaybackState.
    private final MediaControllerCompat.Callback mediaControllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onPlaybackStateChanged(@NonNull PlaybackStateCompat state) {
            Log.d(TAG, "Received playback state change to state " + state.getState());
            MediaControlFragment.this.onPlaybackStateChanged(state);
        }

        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {
            if (metadata == null) {
                return;
            }
            Log.d(TAG, "Received metadata state change to mediaId=" +
                    metadata.getDescription().getMediaId() +
                    " song=" + metadata.getDescription().getTitle());
            MediaControlFragment.this.onMetadataChanged(metadata);
        }
    };


    public MediaControlFragment() {
        // Required empty public constructor
    }

    public static MediaControlFragment newInstance(MediaSessionCompat.Token token) {
        MediaControlFragment instance = new MediaControlFragment();
        instance.playDrawableResource = R.drawable.ic_play_arrow_24dp;
        instance.pauseDrawableResource = R.drawable.ic_pause_24dp;
        instance.token = token;
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_control, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            token = savedInstanceState.getParcelable(STATE_TOKEN);
            pauseDrawableResource = savedInstanceState.getInt(STATE_PAUSE);
            playDrawableResource = savedInstanceState.getInt(STATE_PLAY);
        }

        albumImage = $(view, R.id.albumImage);
        artistText = $(view, R.id.artistText);
        playPauseButton = $(view, R.id.playPauseButton);
        titleText = $(view, R.id.titleText);
        try {
            mediaController = new MediaControllerCompat(getContext(), token);
            mediaController.registerCallback(mediaControllerCallback);
        } catch (RemoteException e) {
            Log.e(TAG, e.getMessage());
        }

        initViews();
    }

    @Override
    public void onDetach() {
        if(mediaController != null) {
            mediaController.unregisterCallback(mediaControllerCallback);
        }
        super.onDetach();
    }

    private void initViews() {
        playPauseButton.setImageResource(playDrawableResource);
        readMetadata(mediaController.getMetadata());
        configureButtons(mediaController.getPlaybackState());
    }

    @SuppressLint("SwitchIntDef")
    private void configureButtons(PlaybackStateCompat state) {
        if (state == null) {
            return;
        }
        boolean enablePlay = false;
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_STOPPED:
                albumImage.setImageResource(R.drawable.ic_album);
                artistText.setText("");
                titleText.setText("");
            case PlaybackStateCompat.STATE_PAUSED:
                enablePlay = true;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                break;
            case PlaybackStateCompat.STATE_ERROR:
                Log.e(TAG, "error playbackstate: " + state.getErrorMessage());
                Toast.makeText(getActivity(), state.getErrorMessage(), Toast.LENGTH_LONG).show();
                break;
        }

        if (enablePlay) {
            playPauseButton.setImageResource(playDrawableResource);
        } else {
            playPauseButton.setImageResource(pauseDrawableResource);
        }

        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPlaybackState() == PlaybackStateCompat.STATE_PLAYING) {
                    mediaController.getTransportControls().pause();
                } else {
                    mediaController.getTransportControls().play();
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_TOKEN, token);
        outState.putInt(STATE_PAUSE, pauseDrawableResource);
        outState.putInt(STATE_PLAY, playDrawableResource);
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
        Log.d(TAG, "onPlaybackStateChanged");

        if (getActivity() == null) {
            Log.w(TAG, "onPlaybackStateChanged called when getActivity null, this should not happen if the callback was properly unregistered. Ignoring.");
            return;
        }

        configureButtons(state);
    }

    private int getPlaybackState() {
        return mediaController.getPlaybackState().getState();
    }
}
