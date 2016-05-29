package be.ugent.zeus.hydra.common.activities;

import com.octo.android.robospice.GsonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

/**
 * This activity is a {@link ToolbarActivity} that also needs a {@link com.octo.android.robospice.SpiceManager} to
 * perform network requests.
 *
 * @link https://github.com/stephanenicolas/robospice/wiki/Starter-guide
 *
 * @author Niko Strijbol
 */
public abstract class SpiceToolbarActivity extends ToolbarActivity {

    /**
     * The default spiceManager uses GSON to parse the result.
     */
    protected SpiceManager spiceManager = new SpiceManager(GsonSpringAndroidSpiceService.class);

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        spiceManager.shouldStop();
        super.onStop();
    }

    public SpiceManager getManager() {
        return spiceManager;
    }
}
