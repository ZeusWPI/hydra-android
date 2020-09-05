package be.ugent.zeus.hydra.association;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class AssociationsRequestTest extends AbstractJsonRequestTest<AssociationList> {

    @Override
    protected String getRelativePath() {
        return "associations.json";
    }

    @Override
    protected JsonOkHttpRequest<AssociationList> getRequest() {
        return new AssociationListRequest.RawRequest(context);
    }
}