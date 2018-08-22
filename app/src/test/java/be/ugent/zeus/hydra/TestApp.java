package be.ugent.zeus.hydra;

import be.ugent.zeus.hydra.testing.Utils;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import io.fabric.sdk.android.Fabric;
import jonathanfinerty.once.Once;

import static be.ugent.zeus.hydra.testing.RobolectricUtils.setupPicasso;

/**
 * Disable some libraries we don't use during Robolectric tests.
 *
 * @author Niko Strijbol
 */
public class TestApp extends HydraApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        // Manually set the theme on the context, since we use it's attributes but don't use an Activity.
        setTheme(R.style.Hydra_Main_NoActionBar);
    }

    @Override
    protected void onCreateInitialise() {
        Once.initialise(this);
        createChannels();

        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(true).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());

        setupPicasso();
    }
}