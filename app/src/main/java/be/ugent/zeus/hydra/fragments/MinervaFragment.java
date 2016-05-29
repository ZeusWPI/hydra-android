package be.ugent.zeus.hydra.fragments;

import be.ugent.zeus.hydra.common.fragments.SpiceFragment;

/**
 * Created by silox on 17/10/15.
 */

public class MinervaFragment extends SpiceFragment {
    @Override
    public void onResume() {
        super.onResume();
        this.sendScreenTracking("Minerva");
    }
}
