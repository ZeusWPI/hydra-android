package be.ugent.zeus.hydra.data.network.requests;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.data.models.specialevent.SpecialEvent;
import be.ugent.zeus.hydra.data.models.specialevent.SpecialEventWrapper;
import be.ugent.zeus.hydra.data.network.Request;
import be.ugent.zeus.hydra.repository.Result;
import be.ugent.zeus.hydra.ui.sko.overview.OverviewActivity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import java8.util.Optional;

import java.util.concurrent.ExecutionException;

/**
 * Request special events and check remote config to enable the special SKO card if needed.
 *
 * @author Niko Strijbol
 */
public class SpecialRemoteEventRequest implements Request<SpecialEventWrapper> {

    private static final String TAG = "RemoteEventRequest";
    private static final String REMOTE_SKO_KEY = "is_sko_card_enabled";

    private final Request<SpecialEventWrapper> wrapping;
    private final FirebaseRemoteConfig config;
    private final Context context;

    public SpecialRemoteEventRequest(Context context, Request<SpecialEventWrapper> request) {
        this.wrapping = request;
        this.context = context.getApplicationContext();
        config = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        config.setDefaults(R.xml.remote_config);
        config.setConfigSettings(configSettings);
    }

    @NonNull
    @Override
    public Result<SpecialEventWrapper> performRequest(Bundle args) {

        Result<SpecialEventWrapper> result = wrapping.performRequest(args);
        Optional<SpecialEvent> skoEvent = maybeAddSko();

        // If the SKO event is present, we add it to the other events.
        return result.apply(specialEventWrapper -> {
            skoEvent.ifPresent(specialEvent -> specialEventWrapper.getSpecialEvents().add(specialEvent));
            return specialEventWrapper;
        });
    }

    private Optional<SpecialEvent> maybeAddSko() {
        //Add the SKO card if necessary.
        try {
            Tasks.await(config.fetch()); //Blocking fetching
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
            return Optional.of(event);
        } else {
            Log.d(TAG, "Not adding SKO card.");
            return Optional.empty();
        }
    }
}