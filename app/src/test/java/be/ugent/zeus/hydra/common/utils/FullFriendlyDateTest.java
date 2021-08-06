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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Collection;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.testing.DateTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;

import static be.ugent.zeus.hydra.common.utils.DateUtils.getDateFormatterForStyle;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@link DateUtils#getFriendlyDate(Context, LocalDate, FormatStyle)} and {@link DateUtils#willBeFriendly(LocalDate)}.
 * <p>
 * The second method is not tested separately to reduce code duplication, since the logic is exactly the same.
 *
 * @author Niko Strijbol
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
public class FullFriendlyDateTest extends DateTest {

    private final FormatStyle style;
    private final LocalDate now = LocalDate.now();
    private final DateTimeFormatter defaultFormatter;


    public FullFriendlyDateTest(FormatStyle style, boolean supportsToday, boolean supportsTomorrow, boolean supportsOvermorrow) {
        super(supportsToday, supportsTomorrow, supportsOvermorrow);
        this.style = style;
        defaultFormatter = getDateFormatterForStyle(style);
    }

    @ParameterizedRobolectricTestRunner.Parameters
    public static Collection<Object[]> parameters() {
        // We use multiple format styles to ensure we don't hard code them and actually adhere to them.
        Object[][] objects = new Object[][]{
                // We only add all truth combinations to the first one, otherwise it adds a lot of tests for nothing.
                {FormatStyle.LONG, true, true, true},
                {FormatStyle.LONG, true, true, false},
                {FormatStyle.LONG, true, false, true},
                {FormatStyle.LONG, true, false, false},
                {FormatStyle.LONG, false, true, true},
                {FormatStyle.LONG, false, true, false},
                {FormatStyle.LONG, false, false, true},
                {FormatStyle.LONG, false, false, false},
                {FormatStyle.MEDIUM, true, true, true},
                {FormatStyle.SHORT, true, true, true}
        };
        return Arrays.asList(objects);
    }

    @Test
    public void todaySupport() {
        String result = DateUtils.getFriendlyDate(c, now, style);
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
        String result = DateUtils.getFriendlyDate(c, tomorrow, style);
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
        String result = DateUtils.getFriendlyDate(c, overmorrow, style);
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
        String result = DateUtils.getFriendlyDate(c, thisWeek, style);
        String expected = thisWeek.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        assertEquals(expected, result);
        assertTrue(DateUtils.willBeFriendly(thisWeek));
    }

    @Test
    public void testNextWeekStartLimit() {
        LocalDate startLimit = now.plusDays(7);
        String result = DateUtils.getFriendlyDate(c, startLimit, style);
        String dayOfWeek = startLimit.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        String expected = c.getString(R.string.date_next_x, dayOfWeek);
        assertEquals(expected, result);
        assertTrue(DateUtils.willBeFriendly(startLimit));
    }

    @Test
    public void testNextWeek() {
        LocalDate thisWeek = now.plusDays(10);
        String result = DateUtils.getFriendlyDate(c, thisWeek, style);
        String dayOfWeek = thisWeek.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        String expected = c.getString(R.string.date_next_x, dayOfWeek);
        assertEquals(expected, result);
        assertTrue(DateUtils.willBeFriendly(thisWeek));
    }

    @Test
    public void testNextWeekEndLimit() {
        LocalDate endLimit = now.plusDays(14);
        String result = DateUtils.getFriendlyDate(c, endLimit, style);
        String expected = defaultFormatter.format(endLimit);
        assertEquals(expected, result);
        assertFalse(DateUtils.willBeFriendly(endLimit));
    }

    @Test
    public void testFarFuture() {
        LocalDate future = now.plusDays(100);
        String result = DateUtils.getFriendlyDate(c, future, style);
        String expected = defaultFormatter.format(future);
        assertEquals(expected, result);
        assertFalse(DateUtils.willBeFriendly(future));
    }

    @Test
    public void testPast() {
        LocalDate past = now.minusDays(100);
        String result = DateUtils.getFriendlyDate(c, past, style);
        String expected = defaultFormatter.format(past);
        assertEquals(expected, result);
        assertFalse(DateUtils.willBeFriendly(past));
    }
}
