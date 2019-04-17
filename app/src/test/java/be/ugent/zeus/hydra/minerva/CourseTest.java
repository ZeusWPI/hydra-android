package be.ugent.zeus.hydra.minerva;

import be.ugent.zeus.hydra.minerva.course.Course;
import be.ugent.zeus.hydra.testing.Utils;
import be.ugent.zeus.hydra.common.ModelTest;
import nl.jqno.equalsverifier.Warning;
import org.junit.Test;

/**
 * @author Niko Strijbol
 */
public class CourseTest extends ModelTest<Course> {

    public CourseTest() {
        super(Course.class);
    }

    @Test
    @Override
    public void equalsAndHash() {
        Utils.defaultVerifier(Course.class)
                .withOnlyTheseFields("id", "order", "ignoreAnnouncements", "ignoreCalendar", "disabledModules")
                .verify();
    }
}