package be.ugent.zeus.hydra.feed;

import be.ugent.zeus.hydra.feed.cards.dismissal.CardIdentifier;
import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class CardIdentifierTest {

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(CardIdentifier.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}