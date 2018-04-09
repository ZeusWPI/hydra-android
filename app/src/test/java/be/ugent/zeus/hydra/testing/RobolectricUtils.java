package be.ugent.zeus.hydra.testing;

import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import be.ugent.zeus.hydra.common.RobolectricTest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Niko Strijbol
 */
public class RobolectricUtils {

    @SuppressWarnings("unchecked")
    public static <T extends View> T inflate(@LayoutRes int layout) {
        return (T) LayoutInflater.from(RuntimeEnvironment.application)
                .inflate(layout, null);
    }

    public static void assertTextIs(String expected, TextView text) {
        assertEquals(expected, text.getText());
    }

    public static void assertNotEmpty(TextView textView) {
        assertNotNull(textView.getText());
        assertNotEquals("", textView.getText());
    }
}
