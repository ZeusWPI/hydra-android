package be.ugent.zeus.hydra.fragments;

import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

import be.ugent.zeus.hydra.HydraApplication;
import be.ugent.zeus.hydra.activities.Hydra;

/**
 * Created by Juta on 03/03/2016.
 */
public abstract class AbstractFragment extends Fragment {
    protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getContext());
    }

    @Override
    public void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    protected void sendScreenTracking(String screenName) {
        HydraApplication app = (HydraApplication) getActivity().getApplication();
        Tracker t = app.getDefaultTracker();
        t.setScreenName(screenName);
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
