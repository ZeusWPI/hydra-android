package be.ugent.zeus.hydra.fragments;

import android.support.v4.app.Fragment;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

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
}
