package be.ugent.zeus.hydra.testing;

import android.app.Application;
import android.content.Context;
import androidx.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ApplicationProvider;

import com.squareup.picasso.PicassoProvider;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
public final class RobolectricUtils {

    private RobolectricUtils() {
        // No instances.
    }

    /**
     * Inflate a view. The view will be inflated in an empty activity context.
     *
     * @param layout The view to inflate.
     * @param <T> Type of the root view.
     *
     * @return The view
     */
    public static <T extends View> T inflate(@LayoutRes int layout) {
        return inflate(getActivityContext(), layout);
    }

    /**
     * Inflate a view.
     *
     * @param context The context to use.
     * @param layout The view to inflate.
     * @param <T> Type of the root view.
     *
     * @return The view
     */
    @SuppressWarnings("unchecked")
    public static <T extends View> T inflate(Context context, @LayoutRes int layout) {
        return (T) LayoutInflater.from(context).inflate(layout, null);
    }

    public static void assertTextIs(String expected, TextView text) {
        assertEquals(expected, text.getText());
    }

    public static void assertNotEmpty(TextView textView) {
        assertNotNull(textView.getText());
        assertNotEquals("", textView.getText());
    }

    @SuppressWarnings("deprecation")
    public static Context getActivityContext() {
        return Robolectric.setupActivity(BlankActivity.class);
    }

    public static ShadowApplication getShadowApplication() {
        return Shadows.shadowOf(ApplicationProvider.<Application>getApplicationContext());
    }

    public static void setupPicasso() {
        Robolectric.setupContentProvider(PicassoProvider.class);
    }
}
