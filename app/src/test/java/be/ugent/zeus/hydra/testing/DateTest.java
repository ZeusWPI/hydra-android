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
