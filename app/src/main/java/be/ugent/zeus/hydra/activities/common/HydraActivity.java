package be.ugent.zeus.hydra.activities.common;

import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.Transition;
import android.view.View;
import be.ugent.zeus.hydra.HydraApplication;

/**
 * Abstract class with common methods.
 *
 * @author Niko Strijbol
 */
public abstract class HydraActivity extends AppCompatActivity {

    /**
     * Finds a view that was identified by the id attribute from the XML that was processed in {@link #onCreate}. This
     * version automatically casts the return value. It cannot be used for null values.
     *
     * @return The view if found or null otherwise.
     */
    @NonNull
    public <T extends View> T $(@IdRes int id) {
        @SuppressWarnings("unchecked")
        T v = (T) findViewById(id);
        assert v != null;
        return v;
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendScreen((HydraApplication) getApplication());
    }

    protected void sendScreen(HydraApplication application) {
        application.sendScreenName(this.getClass().getSimpleName());
    }

    /**
     * Set a custom fade when using transition to prevent white flashing/blinking. This excludes the status bar and
     * navigation bar background from the animation.
     */
    protected void customFade() {
        //Only do it on a version that is high enough.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);
            getWindow().setExitTransition(fade);
            getWindow().setEnterTransition(fade);
        }
    }
}