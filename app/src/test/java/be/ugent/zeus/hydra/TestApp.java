package be.ugent.zeus.hydra;

import jonathanfinerty.once.Once;

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
    }
}