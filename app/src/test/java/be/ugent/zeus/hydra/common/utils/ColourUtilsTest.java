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

package be.ugent.zeus.hydra.common.utils;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.test.core.app.ApplicationProvider;

import be.ugent.zeus.hydra.TestApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(application = TestApp.class)
public class ColourUtilsTest {

    @Test
    public void isDark() {
        Context context = ApplicationProvider.getApplicationContext();
        // Rather simple test to test basics of this simple code.
        assertTrue(ColourUtils.isDark(ContextCompat.getColor(context, android.R.color.black)));
        assertFalse(ColourUtils.isDark(ContextCompat.getColor(context, android.R.color.white)));
    }
}
