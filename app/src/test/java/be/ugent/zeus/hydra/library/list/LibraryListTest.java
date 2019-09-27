package be.ugent.zeus.hydra.library.list;

import be.ugent.zeus.hydra.common.ModelTest;
import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class LibraryListTest extends ModelTest<LibraryList> {

    public LibraryListTest() {
        super(LibraryList.class);
    }

    @Test
    @Override
    public void equalsAndHash() {
        Utils.defaultVerifier(LibraryList.class)
                .withOnlyTheseFields("name", "libraries", "totalLibraries")
                .verify();
    }
}