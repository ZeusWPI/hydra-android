package be.ugent.zeus.hydra.common;

import android.os.Parcelable;

import be.ugent.zeus.hydra.resto.RestoMenu;
import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import static be.ugent.zeus.hydra.testing.Assert.assertParcelable;

/**
 * Generic test class for models.
 *
 * You should initialize the {@link #clazz} field in the constructor or @Before method.
 *
 * This contains tests for {@link android.os.Parcelable}.
 *
 * @author Niko Strijbol
 */
public abstract class ModelTest<T extends Parcelable> {

    private final Class<T> clazz;

    public ModelTest(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Test
    public void parcelable() {
        assertParcelable(clazz);
    }

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(clazz).verify();
    }
}