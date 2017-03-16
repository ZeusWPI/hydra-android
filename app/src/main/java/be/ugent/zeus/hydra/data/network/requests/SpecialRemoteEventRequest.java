package be.ugent.zeus.hydra.data.network.requests;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.activities.sko.OverviewActivity;
import be.ugent.zeus.hydra.data.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.data.models.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.data.models.specialevent.SpecialEvents;
import be.ugent.zeus.hydra.data.network.ProcessableCacheRequest;
import be.ugent.zeus.hydra.data.network.exceptions.RequestFailureException;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.concurrent.ExecutionException;

/**
 * Request special events and check remote config to enable the special SKO card if needed.
 *
 * @author Niko Strijbol
 */
public class SpecialRemoteEventRequest extends ProcessableCacheRequest<SpecialEventWrapper, SpecialEventWrapper> {

    private static final String TAG = "RemoteEventRequest";
    public static final String REMOTE_SKO_KEY = "is_sko_card_enabled";

    private FirebaseRemoteConfig config;
    private RequestFailureException potentialException;

    public SpecialRemoteEventRequest(Context context, boolean shouldRefresh) {
        super(context, new SpecialEventRequest(), shouldRefresh);
        config = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        config.setDefaults(R.xml.remote_config);
        config.setConfigSettings(configSettings);
    }

    @NonNull
    @Override
    public SpecialEventWrapper performRequest() {
        try {
            return super.performRequest();
        } catch (RequestFailureException e) {
            Log.w(TAG, "Error while getting special events, returning empty collection.", e);
            potentialException = e;
            //Empty wrapper.
            SpecialEventWrapper wrapper = new SpecialEventWrapper();
            wrapper.setSpecialEvents(new SpecialEvents());
            return wrapper;
        }
    }

    @NonNull
    @Override
    protected SpecialEventWrapper transform(@NonNull SpecialEventWrapper wrapper) throws RequestFailureException {

        //Add the SKO card if necessary.
        try {
            Tasks.await(config.fetch(86400)); //Blocking fetching
            config.activateFetched();

        } catch (ExecutionException | InterruptedException e) {
            Log.w(TAG, "Error while getting remote config.", e);
        }

        if(config.getBoolean(REMOTE_SKO_KEY) || BuildConfig.DEBUG) {
            Log.d(TAG, "Adding SKO card.");
            SpecialEvent event = new SpecialEvent();
            event.setName("Student Kick-Off");
            event.setSimpleText("Ga naar de info voor de Student Kick-Off");
            event.setImage("http://blog.studentkickoff.be/wp-content/uploads/2016/07/logo.png");
            event.setPriority(1010);
            event.setViewIntent(new Intent(context, OverviewActivity.class));
            //Add to the front.
            wrapper.getSpecialEvents().add(0, event);
        } else {
            Log.d(TAG, "Not adding SKO card.");
        }

        //If the error is set, and the wrapper is empty, we propagate the error.
        //If it is not empty, we return it, since the special event is present.
        if(potentialException != null && wrapper.getSpecialEvents().isEmpty()) {
            throw potentialException;
        } else {
            return wrapper;
        }
    }
}