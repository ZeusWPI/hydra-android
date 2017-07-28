package be.ugent.zeus.hydra.ui.main;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.service.urgent.MusicBinder2;
import be.ugent.zeus.hydra.service.urgent.MusicService2;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class UrgentFragment extends Fragment {

    private static final String TAG = "UrgentFragment";

    private static final String FRAGMENT_TAG = "MediaFragment";

    private static final String CLICKED_STATE = "clicked_state";

    private boolean isBound = false;
    private ServiceConnection serviceConnection = new MusicConnection();
    private MusicService2 musicService;
    private View urgentPlayWrapper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_urgent, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        urgentPlayWrapper = $(view, R.id.urgent_play_wrapper);
    }

    @Override
    public void onStart() {
        super.onStart();

        hideMediaControls();

        // Don't do anything if the service is bound.
        if (!isBound) {
            // Start the service, doesn't matter if it is already started.
            Intent intent = new Intent(getActivity(), MusicService2.class);
            Log.d(TAG, "onStart: Starting service");
            ContextCompat.startForegroundService(getContext(), intent);

            // Request a bind with the service.
            Log.d(TAG, "onStart: Requesting bind");
            getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (isBound) {
            getActivity().unbindService(serviceConnection);
            musicService = null;
        }
    }

    /**
     * Init the media control fragment.
     */
    private void initMediaControls(MediaSessionCompat.Token token) {
        FragmentManager manager = getChildFragmentManager();
        Fragment fragment = manager.findFragmentByTag(FRAGMENT_TAG);
        UrgentControlFragment newFragment = UrgentControlFragment.newInstance(token);
        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        if(fragment == null) {
            t.add(R.id.urgent_fragment_wrapper, newFragment, FRAGMENT_TAG);
        } else {
            t.replace(R.id.urgent_fragment_wrapper, newFragment, FRAGMENT_TAG);
        }
        t.commit();
        urgentPlayWrapper.setVisibility(View.GONE);
    }

    private void hideMediaControls() {
        urgentPlayWrapper.setVisibility(View.VISIBLE);
        FragmentManager manager = getChildFragmentManager();
        Fragment fragment = manager.findFragmentByTag(FRAGMENT_TAG);
        if(fragment != null) {
            getChildFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    /**
     * The service connection. This is used to facilitate communication between the service and use.
     */
    private class MusicConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicBinder2 binder = (MusicBinder2) service;
            musicService = binder.getService();
            musicService.setTokenConsumer(UrgentFragment.this::initMediaControls);
            isBound = true;
            Log.d(TAG, "onServiceConnected: MusicService2 is bound.");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            Log.d(TAG, "onServiceConnected: MusicService2 is unbound.");
        }
    }
}