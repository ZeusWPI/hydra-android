package be.ugent.zeus.hydra.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.inject.Inject;
import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

import be.ugent.android.sdk.UGentApplication;
import be.ugent.android.sdk.oauth.AuthorizationManager;
import be.ugent.android.sdk.oauth.OAuthConfiguration;
import be.ugent.android.sdk.oauth.UGentSpiceService;
import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.activities.Hydra;
import roboguice.RoboGuice;

/**
 * Created by Juta on 03/03/2016.
 */
public abstract class AbstractFragment extends Fragment {
    protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);
    protected SpiceManager minervaSpiceManager = new SpiceManager(UGentSpiceService.class);

    @Inject protected AuthorizationManager authorizationManager;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        RoboGuice.getInjector(getActivity().getApplicationContext()).injectMembersWithoutViews(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RoboGuice.getInjector(getActivity().getApplicationContext()).injectViewMembers(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getContext());
        minervaSpiceManager.start(getContext());
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        minervaSpiceManager.shouldStop();
        super.onStop();
    }

    protected void sendScreenTracking(String screenName) {
        HydraApplication app = (HydraApplication) getActivity().getApplication();
        Tracker t = app.getDefaultTracker();
        t.setScreenName(screenName);
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
