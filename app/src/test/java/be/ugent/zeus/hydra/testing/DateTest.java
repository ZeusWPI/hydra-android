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

import android.content.Context;
import android.content.res.Resources;
import androidx.test.core.app.ApplicationProvider;

import java.util.Locale;

import be.ugent.zeus.hydra.R;
import org.junit.Before;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Common methods for testing with dates.
 *
 * @author Niko Strijbol
 */
public abstract class DateTest {

    protected final boolean supportsToday;
    protected final boolean supportsTomorrow;
    protected final boolean supportsOvermorrow;

    protected Context c;
    protected Locale locale;

    public DateTest(boolean supportsToday, boolean supportsTomorrow, boolean supportsOvermorrow) {
        this.supportsToday = supportsToday;
        this.supportsTomorrow = supportsTomorrow;
        this.supportsOvermorrow = supportsOvermorrow;
    }

    @Before
    public void setUp() {
        // Hack so we don't have to mess with the resources
        c = spy(ApplicationProvider.getApplicationContext());
        Resources resources = spy(ApplicationProvider.getApplicationContext().getResources());
        when(c.getResources()).thenReturn(resources);
        when(resources.getBoolean(R.bool.date_supports_today)).thenReturn(supportsToday);
        when(resources.getBoolean(R.bool.date_supports_tomorrow)).thenReturn(supportsTomorrow);
        when(resources.getBoolean(R.bool.date_supports_overmorrow)).thenReturn(supportsOvermorrow);
        locale = Locale.getDefault();
    }
}
