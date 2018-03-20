package be.ugent.zeus.hydra.minerva.announcement.sync;

import be.ugent.zeus.hydra.minerva.common.AbstractMinervaRequestTest;
import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Niko Strijbol
 */
public class ApiAnnouncementTest {

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(ApiAnnouncement.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .verify();
    }

}