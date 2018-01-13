package be.ugent.zeus.hydra.minerva;

import be.ugent.zeus.hydra.minerva.Module;
import org.junit.Test;

import java.util.Collections;
import java.util.EnumSet;

import static be.ugent.zeus.hydra.testing.Assert.assertCollectionEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Niko Strijbol
 */
public class ModuleTest {

    @Test
    public void emptyNumericalValues() {
        int numericalValue = Module.toNumericalValue(Collections.emptySet());
        EnumSet<Module> enumSet = Module.fromNumericalValue(numericalValue);
        assertTrue(enumSet.isEmpty());
    }

    @Test
    public void testNonEmpty() {
        EnumSet<Module> expected = EnumSet.allOf(Module.class);
        int value = Module.toNumericalValue(expected);
        EnumSet<Module> actual = Module.fromNumericalValue(value);
        assertCollectionEquals(expected, actual);
    }
}