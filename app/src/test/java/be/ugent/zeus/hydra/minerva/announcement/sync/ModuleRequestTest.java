package be.ugent.zeus.hydra.minerva.announcement.sync;

import be.ugent.zeus.hydra.minerva.common.AbstractMinervaRequestTest;
import be.ugent.zeus.hydra.minerva.common.MinervaRequest;
import be.ugent.zeus.hydra.minerva.course.Course;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static be.ugent.zeus.hydra.testing.Utils.generate;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class ModuleRequestTest extends AbstractMinervaRequestTest<ApiTools> {

    private final Course course = generate(Course.class);

    @Override
    protected String getRelativePath() {
        return "minerva/tools.json";
    }

    @Override
    protected MinervaRequest<ApiTools> getRequest() {
        return new ModuleRequest(RuntimeEnvironment.application, getAccount(), course);
    }
}