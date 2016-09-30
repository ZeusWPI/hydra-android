package be.ugent.zeus.hydra.requests;

import android.support.annotation.NonNull;
import android.util.Log;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.models.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.models.specialevent.SpecialEvents;
import be.ugent.zeus.hydra.requests.common.Request;
import be.ugent.zeus.hydra.requests.exceptions.RequestFailureException;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.concurrent.ExecutionException;

/**
 * Request special events and check remote config to enable the special SKO card if needed.
 *
 * @author Niko Strijbol
 */
public class SpecialRemoteEventRequest implements Request<SpecialEventWrapper> {

    private static final String TAG = "RemoteEventRequest";
    public static final String REMOTE_SKO_KEY = "is_sko_card_enabled";

    private FirebaseRemoteConfig config;
    private SpecialEventRequest request;

    public SpecialRemoteEventRequest() {
        request = new SpecialEventRequest();
        config = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        config.setDefaults(R.xml.remote_config);
        config.setConfigSettings(configSettings);
    }

    @NonNull
    @Override
    public SpecialEventWrapper performRequest() throws RequestFailureException {

        //Empty wrapper.
        SpecialEventWrapper wrapper = new SpecialEventWrapper();
        wrapper.setSpecialEvents(new SpecialEvents());

        //Add the SKO card if necessary.
        try {
            Tasks.await(config.fetch(86400)); //Blocking fetching
            config.activateFetched();

        } catch (ExecutionException | InterruptedException e) {
            Log.w(TAG, "Error while getting remote config.", e);
        }

        if(config.getBoolean(REMOTE_SKO_KEY)) {
            Log.d(TAG, "Adding SKO card.");
            SpecialEvent event = new SpecialEvent();
            event.setName("Student Kick-Off");
            event.setSimpleText("Ga naar de info voor de Student Kick-Off");
            event.setImage("http://blog.studentkickoff.be/wp-content/uploads/2016/07/logo.png");
            event.setSko(true);
            event.setPriority(1010);
            wrapper.getSpecialEvents().add(event);
        } else {
            Log.d(TAG, "Not adding SKO card.");
        }

        //Add normal special events.
        try {
            wrapper.getSpecialEvents().addAll(request.performRequest().getSpecialEvents());
        } catch (RequestFailureException e) {
            //If there are no special events, we pass along the error.
            if(wrapper.getSpecialEvents().isEmpty()) {
                throw e;
            }
        }

        return wrapper;
    }
}