package be.ugent.zeus.hydra.news;

import be.ugent.zeus.hydra.testing.Utils;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class NewsStreamTest {
    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(NewsStream.class)
                .withNonnullFields("id", "updated", "entries")
                .verify();
    }
}