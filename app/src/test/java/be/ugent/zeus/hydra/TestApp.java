package be.ugent.zeus.hydra;

import be.ugent.zeus.hydra.common.reporting.Manager;
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
        setTheme(R.style.Hydra_DayNight);
    }

    @Override
    protected void onCreateInitialise() {
        Once.initialise(this);

        Manager.saveAnalyticsPermission(this, false);
        Manager.saveCrashReportingPermission(this, false);
        Manager.syncPermissions(this);

        setupPicasso();

        // Do not use SSL
        System.setProperty("javax.net.ssl.trustStore", "NONE");
    }
}
