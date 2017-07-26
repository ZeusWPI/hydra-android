package be.ugent.zeus.hydra.data.models.minerva;

import be.ugent.zeus.hydra.testing.Utils;
import be.ugent.zeus.hydra.data.models.ModelTest;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class AnnouncementTest extends ModelTest<Announcement> {

    public AnnouncementTest() {
        super(Announcement.class);
    }

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(Announcement.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .withOnlyTheseFields("itemId", "read")
                .verify();
    }
}