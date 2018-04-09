package be.ugent.zeus.hydra.minerva.announcement.sync;

import be.ugent.zeus.hydra.minerva.common.AbstractMinervaRequestTest;
import be.ugent.zeus.hydra.minerva.course.Course;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static be.ugent.zeus.hydra.testing.Utils.generate;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertThat;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class WhatsNewRequestTest extends AbstractMinervaRequestTest<ApiWhatsNew> {

    private Course course = generate(Course.class);

    @Override
    protected WhatsNewRequest getRequest() {
        return new WhatsNewRequest(course, RuntimeEnvironment.application, getAccount());
    }

    @Override
    protected String getRelativePath() {
        return "minerva/whatsnew.json";
    }

    @Test
    public void testUrlParams() {
        WhatsNewRequest request = getRequest();
        assertThat(request.getAPIUrl(), endsWith("course/" + course.getId() + "/whatsnew"));
    }
}