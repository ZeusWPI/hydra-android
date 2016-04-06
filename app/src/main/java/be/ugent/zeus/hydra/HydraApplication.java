package be.ugent.zeus.hydra;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by feliciaan on 06/04/16.
 */
public class HydraApplication extends Application {

    @Override
    public void onCreate()
    {
        super.onCreate();

        JodaTimeAndroid.init(this);
    }
}
