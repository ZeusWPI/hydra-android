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
import android.content.res.Resources;
import androidx.test.core.app.ApplicationProvider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.testing.DateTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;

import static be.ugent.zeus.hydra.common.utils.DateUtils.dateFormatterForStyle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link DateUtils#friendlyDate(Context, LocalDate)}.
 *
 * @author Niko Strijbol
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
public class FriendlyDateTest extends DateTest {

    private final LocalDate now = LocalDate.now();
    private final DateTimeFormatter defaultFormatter = dateFormatterForStyle(FormatStyle.MEDIUM);

    public FriendlyDateTest(boolean supportsToday, boolean supportsTomorrow, boolean supportsOvermorrow) {
        super(supportsToday, supportsTomorrow, supportsOvermorrow);
    }

    @ParameterizedRobolectricTestRunner.Parameters
    public static Collection<Object[]> parameters() {
        // We use multiple format styles to ensure we don't hard code them and actually adhere to them.
        Object[][] objects = new Object[][]{
                // We only add all truth combinations to the first one, otherwise it adds a lot of tests for nothing.
                {true, true, true},
                {true, true, false},
                {true, false, true},
                {true, false, false},
                {false, true, true},
                {false, true, false},
                {false, false, true},
                {false, false, false},
                {true, true, true},
                {true, true, true}
        };
        return Arrays.asList(objects);
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

    @Test
    public void todaySupport() {
        String result = DateUtils.friendlyDate(c, now);
        String expected;
        if (supportsToday) {
            expected = c.getString(R.string.date_today);
        } else {
            expected = now.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        }
        assertEquals(expected, result);
        assertTrue(DateUtils.willBeFriendly(now));
    }

    @Test
    public void tomorrowSupport() {
        LocalDate tomorrow = now.plusDays(1);
        String result = DateUtils.friendlyDate(c, tomorrow);
        String expected;
        if (supportsTomorrow) {
            expected = c.getString(R.string.date_tomorrow);
        } else {
            expected = tomorrow.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        }
        assertEquals(expected, result);
        assertTrue(DateUtils.willBeFriendly(tomorrow));
    }

    @Test
    public void testOvermorrowSupport() {
        LocalDate overmorrow = now.plusDays(2);
        String result = DateUtils.friendlyDate(c, overmorrow);
        String expected;
        if (supportsOvermorrow) {
            expected = c.getString(R.string.date_overmorrow);
        } else {
            expected = overmorrow.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        }
        assertEquals(expected, result);
        assertTrue(DateUtils.willBeFriendly(overmorrow));
    }

    @Test
    public void testThisWeek() {
        LocalDate thisWeek = now.plusDays(3);
        String result = DateUtils.friendlyDate(c, thisWeek);
        String expected = thisWeek.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        assertEquals(expected, result);
        assertTrue(DateUtils.willBeFriendly(thisWeek));
    }

    @Test
    public void testNextWeekStartLimit() {
        LocalDate startLimit = now.plusDays(7);
        String result = DateUtils.friendlyDate(c, startLimit);
        String dayOfWeek = startLimit.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        String expected = c.getString(R.string.date_next_x, dayOfWeek);
        assertEquals(expected, result);
        assertTrue(DateUtils.willBeFriendly(startLimit));
    }

    @Test
    public void testNextWeek() {
        LocalDate thisWeek = now.plusDays(10);
        String result = DateUtils.friendlyDate(c, thisWeek);
        String dayOfWeek = thisWeek.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        String expected = c.getString(R.string.date_next_x, dayOfWeek);
        assertEquals(expected, result);
        assertTrue(DateUtils.willBeFriendly(thisWeek));
    }

    @Test
    public void testNextWeekEndLimit() {
        LocalDate endLimit = now.plusDays(14);
        String result = DateUtils.friendlyDate(c, endLimit);
        String expected = defaultFormatter.format(endLimit);
        assertEquals(expected, result);
        assertFalse(DateUtils.willBeFriendly(endLimit));
    }

    @Test
    public void testFarFuture() {
        LocalDate future = now.plusDays(100);
        String result = DateUtils.friendlyDate(c, future);
        String expected = defaultFormatter.format(future);
        assertEquals(expected, result);
        assertFalse(DateUtils.willBeFriendly(future));
    }


    @Test
    public void testPast() {
        LocalDate past = now.minusDays(100);
        String result = DateUtils.friendlyDate(c, past);
        String expected = defaultFormatter.format(past);
        assertEquals(expected, result);
        assertFalse(DateUtils.willBeFriendly(past));
    }
}
