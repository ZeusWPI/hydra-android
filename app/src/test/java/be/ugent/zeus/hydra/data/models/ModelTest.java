package be.ugent.zeus.hydra.data.models;

import android.os.Parcelable;
import org.junit.Test;

import java.io.Serializable;

import static be.ugent.zeus.hydra.testing.Assert.assertParcelable;
import static be.ugent.zeus.hydra.testing.Assert.assertSerialization;

/**
 * Generic test class for models.
 *
 * You should initialize the {@link #clazz} field in the constructor or @Before method.
 *
 * This contains tests for {@link java.io.Serializable}, {@link android.os.Parcelable}.
 *
 * @author Niko Strijbol
 */
public abstract class ModelTest<T extends Parcelable & Serializable> {

    protected final Class<T> clazz;

    public ModelTest(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Test
    public void parcelable() {
        assertParcelable(clazz);
    }

    @Test
    public void serialize() {
        assertSerialization(clazz);
    }
}