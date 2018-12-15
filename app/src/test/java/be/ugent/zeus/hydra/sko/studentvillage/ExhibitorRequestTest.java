package be.ugent.zeus.hydra.sko.studentvillage;

import java.util.List;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class ExhibitorRequestTest extends AbstractJsonRequestTest<List<Exhibitor>> {

    @Override
    protected String getRelativePath() {
        return "sko_student_village.json";
    }

    @Override
    protected JsonOkHttpRequest<List<Exhibitor>> getRequest() {
        return new ExhibitorRequest(context);
    }
}