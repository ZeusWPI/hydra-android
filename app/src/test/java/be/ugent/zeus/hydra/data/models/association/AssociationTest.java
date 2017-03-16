package be.ugent.zeus.hydra.data.models.association;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link Association}.
 *
 * @author Niko Strijbol
 */
public class AssociationTest {

    @Test
    public void name() {
        Association full = Association.create("TEST", "Test", "Tester", null);
        assertEquals(full.fullName(), full.name());
        Association partial = Association.create("TEST", null, "Tester", null);
        assertEquals(partial.displayName(), partial.name());
    }

    @Test
    public void create() {
        Association full = Association.create("TEST", "Test", "Tester", "another");
        assertEquals("TEST", full.internalName());
        assertEquals("Test", full.fullName());
        assertEquals("Tester", full.displayName());
        assertEquals("another", full.parentAssociation());
    }

    @Test
    public void imageLink() {
        Association full = Association.create("TEST", "Test", "Tester", "another");
        assertEquals(String.format(Association.IMAGE_URL, "test"), full.imageLink());
    }
}