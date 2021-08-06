/*
 * Copyright (c) 2021 The Hydra authors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package be.ugent.zeus.hydra.testing;

import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.LayoutRes;
import androidx.test.core.app.ApplicationProvider;

import com.squareup.picasso.PicassoProvider;
import org.robolectric.Robolectric;
import org.robolectric.Shadows;
import org.robolectric.shadows.ShadowApplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

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
     * @param <T>    Type of the root view.
     * @return The view
     */
    public static <T extends View> T inflate(@LayoutRes int layout) {
        return inflate(getActivityContext(), layout);
    }

    /**
     * Inflate a view.
     *
     * @param context The context to use.
     * @param layout  The view to inflate.
     * @param <T>     Type of the root view.
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
