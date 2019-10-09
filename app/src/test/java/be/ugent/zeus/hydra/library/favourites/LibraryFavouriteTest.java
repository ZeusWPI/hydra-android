package be.ugent.zeus.hydra.library.favourites;

import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class LibraryFavouriteTest {

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(LibraryFavourite.class)
                .withIgnoredFields("name")
                .verify();
    }

}