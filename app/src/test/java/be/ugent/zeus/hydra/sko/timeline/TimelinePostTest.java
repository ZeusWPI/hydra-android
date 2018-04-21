package be.ugent.zeus.hydra.sko.timeline;

import be.ugent.zeus.hydra.common.ModelTest;
import be.ugent.zeus.hydra.testing.Utils;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class TimelinePostTest extends ModelTest<TimelinePost> {

    public TimelinePostTest() {
        super(TimelinePost.class);
    }

    @Test
    public void equalsAndHash() {
        Utils.defaultVerifier(TimelinePost.class)
                .suppress(Warning.NONFINAL_FIELDS)
                .withOnlyTheseFields("id", "title", "body", "postType", "createdAt")
                .verify();
    }
}