package be.ugent.zeus.hydra.models.association;

import be.ugent.zeus.hydra.testing.Utils;
import be.ugent.zeus.hydra.models.ModelTest;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class NewsItemTest extends ModelTest<NewsItem> {

    public NewsItemTest() {
        super(NewsItem.class);
    }

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(NewsItem.class)
                .withOnlyTheseFields("id")
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}