package be.ugent.zeus.hydra.preferences;

import org.junit.Test;

import static be.ugent.zeus.hydra.testing.Assert.assertParcelable;

public class PreferenceEntryTest {

    @Test
    public void parcelable() {
        assertParcelable(PreferenceEntry.class);
    }
}
