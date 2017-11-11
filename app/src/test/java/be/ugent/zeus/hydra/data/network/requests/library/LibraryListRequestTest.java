package be.ugent.zeus.hydra.data.network.requests.library;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.domain.models.library.LibraryList;
import be.ugent.zeus.hydra.data.network.AbstractJsonSpringRequestTest;
import be.ugent.zeus.hydra.data.network.JsonSpringRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class LibraryListRequestTest extends AbstractJsonSpringRequestTest<LibraryList> {

    public LibraryListRequestTest() {
        super(LibraryList.class);
    }

    @Override
    protected Resource getSuccessResponse() {
        return new ClassPathResource("all_libraries.json");
    }

    @Override
    protected JsonSpringRequest<LibraryList> getRequest() {
        return new LibraryListRequest();
    }
}