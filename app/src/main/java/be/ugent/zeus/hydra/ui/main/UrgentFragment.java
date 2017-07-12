package be.ugent.zeus.hydra.ui.main;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.UrgentTrack;
import be.ugent.zeus.hydra.service.urgent.BoundServiceCallback;
import be.ugent.zeus.hydra.service.urgent.MusicBinder;
import be.ugent.zeus.hydra.service.urgent.MusicCallback;
import be.ugent.zeus.hydra.service.urgent.MusicService;

import static be.ugent.zeus.hydra.ui.common.ViewUtils.$;

/**
 * @author Niko Strijbol
 */
public class UrgentFragment extends Fragment implements MusicCallback, BoundServiceCallback {

    private static final String TAG = "UrgentFragment";

    private static final String FRAGMENT_TAG = "MediaFragment";

    private boolean isBound = false;
    private ServiceConnection serviceConnection = new MusicConnection();
    private MusicService musicService;
    private View urgentPlayWrapper;

    private boolean freshStart = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_urgent, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        urgentPlayWrapper = $(view, R.id.urgent_play_wrapper);
        $(view, R.id.urgent_play).setOnClickListener(v -> {
            //We are already bound.
            if (isBound) {
                beginPlaying();
            } else {
                //The callback will start playing.
                freshStart = true;
                bind();
            }
        });
        //If the service is running, request a bind.
        if (MusicService.isRunning(getContext())) {
            bind();
        }
    }

    @Override
    public void onStop() {
        Log.d(TAG, "Stopped urgent.");
        unbind();
        super.onStop();
    }

    @Override
    public void requestUnbind() {
        unbind();
        hideMediaControls();
    }

    @Override
    public void onResume() {
        super.onResume();
        //If the service is running, request a bind.
        if(MusicService.isRunning(getContext())) {
            bind();
        } else {
            hideMediaControls();
        }
    }

    /**
     * Bind the {@link MusicService}. If it is not running, we will start it as well. If there already is a bound
     * service, this method will do nothing.
     */
    private void bind() {

        //If it is already bound, do nothing.
        if(isBound) {
            return;
        }

        Intent intent = new Intent(getActivity(), MusicService.class);

        //If it doesn't run, start it.
        if(!MusicService.isRunning(getContext())) {
            // Use the compat version, since Android O and up require some changes.
            ContextCompat.startForegroundService(getContext(), intent);
        }

        //Bind the service
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Unbind the service. This will only do something if there is a bound service.
     */
    private void unbind() {

        //If there is no service, do nothing.
        if(!isBound) {
            return;
        }

        getActivity().unbindService(serviceConnection);
        //There is no callback for unbound services, so we manually call one.
        onUnbind();
    }

    /**
     * Called when the service is disconnected.
     */
    private void onUnbind() {
        musicService.setCallback(null);
        musicService.setBoundCallback(null);
        musicService = null;
        isBound = false;
    }

    /**
     * Start playing Urgent.fm if it is not already. This method requires a bound service.
     */
    private void beginPlaying() {

        //Add the track if needed.
        if(!hasUrgent()) {
            musicService.getTrackManager().clear();
            musicService.getTrackManager().addTrack(new UrgentTrack(getContext()));
        }

        //If we are not playing, attempt to start playing.
        if(musicService.isPlaying()) {
            Log.d(TAG, "Urgent is already playing!");
        } else {
            try {
                musicService.startPlaying();
            } catch (Exception e) {
                Log.e("UrgentFragment", "Error while playing.", e);
            }
        }
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
     * Init the media control fragment.
     */
    private void initMediaControls() {
        FragmentManager manager = getChildFragmentManager();
        Fragment fragment = manager.findFragmentByTag(FRAGMENT_TAG);
        Fragment newFragment = UrgentControlFragment.newInstance(musicService.getMediaToken());
        FragmentTransaction t = getChildFragmentManager().beginTransaction();
        if(fragment == null) {
            t.add(R.id.urgent_fragment_wrapper, newFragment, FRAGMENT_TAG);
        } else {
            t.replace(R.id.urgent_fragment_wrapper, newFragment, FRAGMENT_TAG);
        }
        t.commit();
        urgentPlayWrapper.setVisibility(View.GONE);
    }

    @Override
    public void onPermissionRequired(final int requestCode, final String permission, String rationale) {
        // Should we show an explanation?
        if (shouldShowRequestPermissionRationale(permission)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(rationale);
            builder.setNeutralButton("Got it", (dialog, which) -> askForPermission(requestCode, permission));
            builder.create().show();
        } else {
            // No explanation needed, we can request the permission.
            askForPermission(requestCode, permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(isBound){
            musicService.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    private void askForPermission(int requestCode, String permission){
        requestPermissions(new String[]{permission}, requestCode);
    }

    @Override
    public void onLoading() {
        assert getView() != null;
        Snackbar.make(getView(), R.string.urgent_loading, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onPlaybackStarted() {

    }

    @Override
    public void onPlaybackStopped() {

    }

    @Override
    public void onError() {
        Toast.makeText(getContext().getApplicationContext(), R.string.oops, Toast.LENGTH_SHORT).show();
    }

    /**
     * Check if urgent was added. This needs a bound service!
     *
     * @return If the service has Urgent as track.
     */
    private boolean hasUrgent() {
        return musicService.getTrackManager().hasTracks();
    }

    /**
     * The service connection. This is used to facilitate communication between the service and use.
     */
    private class MusicConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            MusicBinder binder = (MusicBinder) service;
            musicService = binder.getService();
            musicService.setBoundCallback(UrgentFragment.this);
            musicService.setCallback(UrgentFragment.this);
            Intent startThis = new Intent(getActivity(), MainActivity.class);
            startThis.putExtra(MainActivity.ARG_TAB, R.id.drawer_urgent);
            PendingIntent i = PendingIntent.getActivity(getContext(), 0, startThis, PendingIntent.FLAG_UPDATE_CURRENT);
            musicService.getNotificationManager().setContentIntent(i);
            musicService.getNotificationManager().setIcon(R.drawable.ic_notification_urgent);
            initMediaControls();
            isBound = true;
            if (freshStart) {
                freshStart = false;
                beginPlaying();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            onUnbind();
        }
    }
}