package be.ugent.zeus.hydra;

import android.content.Context;

import be.ugent.zeus.hydra.common.reporting.Reporting;
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
    protected void onAttachBaseContextInitialize(Context base) {
        // Do nothing.
    }

    @Override
    protected void onCreateInitialise() {
        Once.initialise(this);

        Reporting.saveAnalyticsPermission(this, false);
        Reporting.saveCrashReportingPermission(this, false);
        Reporting.syncPermissions(this);

        setupPicasso();

        // Do not use SSL
        System.setProperty("javax.net.ssl.trustStore", "NONE");
    }
}
