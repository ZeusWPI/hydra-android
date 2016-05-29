package be.ugent.zeus.hydra.common.fragments;

import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

import be.ugent.zeus.hydra.HydraApplication;

/**
 * This is a fragment that supports requests with spice.
 *
 * @author Niko Strijbol
 * @author Juta
 */
public abstract class SpiceFragment extends Fragment {

    /**
     * The default spiceManager uses GSON to parse the result.
     */
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

    /**
     * @return The manager for the request.
     */
    public SpiceManager getManager() {
        return spiceManager;
    }

    //TODO: this
    protected void sendScreenTracking(String screenName) {
        HydraApplication app = (HydraApplication) getActivity().getApplication();
        Tracker t = app.getDefaultTracker();
        t.setScreenName(screenName);
        t.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
