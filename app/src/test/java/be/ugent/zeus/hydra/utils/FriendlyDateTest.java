package be.ugent.zeus.hydra.utils;

import android.content.Context;
import android.content.res.Resources;

import androidx.test.core.app.ApplicationProvider;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import be.ugent.zeus.hydra.R;
import be.ugent.zeus.hydra.testing.DateTest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.ParameterizedRobolectricTestRunner;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.FormatStyle;
import org.threeten.bp.format.TextStyle;

import static be.ugent.zeus.hydra.utils.DateUtils.getDateFormatterForStyle;
import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Tests for the {@link DateUtils#getFriendlyDate(Context, LocalDate)}.
 *
 * @author Niko Strijbol
 */
@RunWith(ParameterizedRobolectricTestRunner.class)
public class FriendlyDateTest extends DateTest {

    private final LocalDate now = LocalDate.now();
    private final DateTimeFormatter defaultFormatter = getDateFormatterForStyle(FormatStyle.MEDIUM);

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

    public FriendlyDateTest(boolean supportsToday, boolean supportsTomorrow, boolean supportsOvermorrow) {
        super(supportsToday, supportsTomorrow, supportsOvermorrow);
    }

    @Test
    public void todaySupport() {
        String result = DateUtils.getFriendlyDate(c, now);
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
        String result = DateUtils.getFriendlyDate(c, tomorrow);
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
        String result = DateUtils.getFriendlyDate(c, overmorrow);
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
        String result = DateUtils.getFriendlyDate(c, thisWeek);
        String expected = thisWeek.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        assertEquals(expected, result);
        assertTrue(DateUtils.willBeFriendly(thisWeek));
    }

    @Test
    public void testNextWeekStartLimit() {
        LocalDate startLimit = now.plusDays(7);
        String result = DateUtils.getFriendlyDate(c, startLimit);
        String dayOfWeek = startLimit.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        String expected = c.getString(R.string.date_next_x, dayOfWeek);
        assertEquals(expected, result);
        assertTrue(DateUtils.willBeFriendly(startLimit));
    }

    @Test
    public void testNextWeek() {
        LocalDate thisWeek = now.plusDays(10);
        String result = DateUtils.getFriendlyDate(c, thisWeek);
        String dayOfWeek = thisWeek.getDayOfWeek().getDisplayName(TextStyle.FULL, locale);
        String expected = c.getString(R.string.date_next_x, dayOfWeek);
        assertEquals(expected, result);
        assertTrue(DateUtils.willBeFriendly(thisWeek));
    }

    @Test
    public void testNextWeekEndLimit() {
        LocalDate endLimit = now.plusDays(14);
        String result = DateUtils.getFriendlyDate(c, endLimit);
        String expected = defaultFormatter.format(endLimit);
        assertEquals(expected, result);
        assertFalse(DateUtils.willBeFriendly(endLimit));
    }

    @Test
    public void testFarFuture() {
        LocalDate future = now.plusDays(100);
        String result = DateUtils.getFriendlyDate(c, future);
        String expected = defaultFormatter.format(future);
        assertEquals(expected, result);
        assertFalse(DateUtils.willBeFriendly(future));
    }


    @Test
    public void testPast() {
        LocalDate past = now.minusDays(100);
        String result = DateUtils.getFriendlyDate(c, past);
        String expected = defaultFormatter.format(past);
        assertEquals(expected, result);
        assertFalse(DateUtils.willBeFriendly(past));
    }
}