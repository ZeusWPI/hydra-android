package be.ugent.zeus.hydra.data.models.association;

import be.ugent.zeus.hydra.data.models.ModelTest;
import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class UgentNewsItemTest extends ModelTest<UgentNewsItem> {

    public UgentNewsItemTest() {
        super(UgentNewsItem.class);
    }

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(UgentNewsItem.class)
                .withOnlyTheseFields("identifier")
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }
}