package be.ugent.zeus.hydra.library.list;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.common.network.JsonOkHttpRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class LibraryListRequestTest extends AbstractJsonRequestTest<LibraryList> {

    @Override
    protected String getRelativePath() {
        return "all_libraries.json";
    }

    @Override
    protected JsonOkHttpRequest<LibraryList> getRequest() {
        return new LibraryListRequest(RuntimeEnvironment.application);
    }
}