/**
Copyright 2016 Allan Pichardo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.mylovemhz.simplay;

import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;


public class MediaControlFragment extends Fragment{

    private static final String TAG = "MediaControlFragment";
    private static final String STATE_TOKEN = "state_token";
    private static final String STATE_PREVIOUS = "state_previous";
    private static final String STATE_NEXT = "state_next";
    private static final String STATE_PLAY = "state_play";
    private static final String STATE_PAUSE = "state_pause";
    private static final long INTERVAL_SEEKBAR = 1000 / 60;

    private ImageButton playPauseButton;
    private ImageButton previousButton;
    private ImageButton nextButton;
    private TextView artistText;
    private TextView titleText;
    private ImageView albumImage;
    private SeekBar seekBar;
    private MediaControllerCompat mediaController;
    private MediaSessionCompat.Token token;

    private int playDrawableResource;
    private int pauseDrawableResource;
    private int nextDrawable;
    private int previousDrawable;

    private Handler handler = new Handler();
    private Runnable seekTickRunnable = new Runnable() {
        @Override
        public void run() {
            if(mediaController.getPlaybackState() != null) {
                seekBar.setSecondaryProgress((int) mediaController.getPlaybackState().getBufferedPosition());
                if (getPlaybackState() == PlaybackStateCompat.STATE_PLAYING) {
                    seekBar.setProgress((int) mediaController.getPlaybackState().getPosition());
                }
                handler.postDelayed(this,INTERVAL_SEEKBAR);
            }
        }
    };

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

    public static MediaControlFragment newInstance(MediaSessionCompat.Token token,
                                                   int playDrawableResource,
                                                   int pauseDrawableResource,
                                                   int nextDrawable,
                                                   int previousDrawable){
        MediaControlFragment instance = new MediaControlFragment();
        instance.playDrawableResource = playDrawableResource;
        instance.pauseDrawableResource = pauseDrawableResource;
        instance.nextDrawable = nextDrawable;
        instance.previousDrawable = previousDrawable;
        instance.token = token;
        return instance;
    }

    public static MediaControlFragment newInstance(MediaSessionCompat.Token token){
        return MediaControlFragment.newInstance(token, R.drawable.ic_play_arrow_24dp, R.drawable.ic_pause_24dp,
                R.drawable.ic_skip_next_24dp, R.drawable.ic_skip_previous_24dp);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_media_control, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if(savedInstanceState != null){
            token = savedInstanceState.getParcelable(STATE_TOKEN);
            previousDrawable = savedInstanceState.getInt(STATE_PREVIOUS);
            pauseDrawableResource = savedInstanceState.getInt(STATE_PAUSE);
            playDrawableResource = savedInstanceState.getInt(STATE_PLAY);
            nextDrawable = savedInstanceState.getInt(STATE_NEXT);
        }

        albumImage = (ImageView)view.findViewById(R.id.albumImage);
        artistText = (TextView)view.findViewById(R.id.artistText);
        nextButton = (ImageButton)view.findViewById(R.id.nextButton);
        playPauseButton = (ImageButton)view.findViewById(R.id.playPauseButton);
        previousButton = (ImageButton)view.findViewById(R.id.previousButton);
        seekBar = (SeekBar)view.findViewById(R.id.seekBar);
        titleText = (TextView)view.findViewById(R.id.titleText);
        try {
            mediaController = new MediaControllerCompat(getContext(),token);
            mediaController.registerCallback(mediaControllerCallback);
        } catch (RemoteException e) {
            Log.e(TAG,e.getMessage());
        }

        initViews();
    }

    private void initViews(){
        nextButton.setImageResource(nextDrawable);
        playPauseButton.setImageResource(playDrawableResource);
        previousButton.setImageResource(previousDrawable);

        readMetadata(mediaController.getMetadata());
        configureButtons(mediaController.getPlaybackState());
    }

    private void configureButtons(PlaybackStateCompat state){
        if (state == null) {
            return;
        }
        boolean enablePlay = false;
        switch (state.getState()) {
            case PlaybackStateCompat.STATE_STOPPED:
                albumImage.setImageResource(R.drawable.ic_album_24dp);
                artistText.setText("");
                titleText.setText("");
                seekBar.setProgress(0);
                seekBar.setMax(1);
            case PlaybackStateCompat.STATE_PAUSED:
                handler.removeCallbacks(seekTickRunnable);
                enablePlay = true;
                break;
            case PlaybackStateCompat.STATE_PLAYING:
                handler.post(seekTickRunnable);
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
                if (getPlaybackState() != PlaybackStateCompat.STATE_PLAYING) {
                    mediaController.getTransportControls().play();
                } else {
                    mediaController.getTransportControls().pause();
                }
            }
        });
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaController.getTransportControls().skipToPrevious();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaController.getTransportControls().skipToNext();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mediaController.getTransportControls().seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(STATE_TOKEN, token);
        outState.putInt(STATE_PREVIOUS,previousDrawable);
        outState.putInt(STATE_PAUSE,pauseDrawableResource);
        outState.putInt(STATE_PLAY,playDrawableResource);
        outState.putInt(STATE_NEXT,nextDrawable);
        super.onSaveInstanceState(outState);
    }

    private void readMetadata(MediaMetadataCompat metadata){
        if(metadata != null) {
            seekBar.setMax((int) metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION));
            artistText.setText(metadata.getText(MediaMetadataCompat.METADATA_KEY_ALBUM_ARTIST));
            titleText.setText(metadata.getText(MediaMetadataCompat.METADATA_KEY_TITLE));
            String artUrl = metadata.getText(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI).toString();
            try {
                Picasso.with(getContext().getApplicationContext())
                        .load(artUrl)
                        .into(albumImage);
            } catch (IllegalArgumentException e) {
                //no artwork
                albumImage.setImageResource(R.drawable.ic_album_24dp);
            }
        }
    }

    private void onMetadataChanged(MediaMetadataCompat metadata) {
        Log.d(TAG, "onMetadataChanged ");
        if (getActivity() == null) {
            Log.w(TAG, "onMetadataChanged called when getActivity null," +
                    "this should not happen if the callback was properly unregistered. Ignoring.");
            return;
        }
        if (metadata == null) {
            return;
        }

        readMetadata(metadata);
    }

    private void onPlaybackStateChanged(PlaybackStateCompat state) {
        Log.d(TAG, "onPlaybackStateChanged ");
        if (getActivity() == null) {
            Log.w(TAG, "onPlaybackStateChanged called when getActivity null," +
                    "this should not happen if the callback was properly unregistered. Ignoring.");
            return;
        }

        configureButtons(state);
    }

    public int getPlaybackState(){
        return mediaController.getPlaybackState().getState();
    }
}
