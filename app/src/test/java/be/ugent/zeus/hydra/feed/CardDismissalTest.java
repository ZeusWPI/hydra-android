package be.ugent.zeus.hydra.feed;

import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class CardDismissalTest {

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(CardDismissal.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}