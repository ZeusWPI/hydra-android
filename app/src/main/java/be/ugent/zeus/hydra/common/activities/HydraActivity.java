package be.ugent.zeus.hydra.common.activities;

import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Abstract class with common methods.
 *
 * @author Niko Strijbol
 */
public abstract class HydraActivity extends AppCompatActivity {

    /**
     * Finds a view that was identified by the id attribute from the XML that was processed in {@link #onCreate}. This
     * version automatically casts the return value.
     *
     * @return The view if found or null otherwise.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends View> T $(@IdRes int id) {
        return (T) findViewById(id);
    }
}