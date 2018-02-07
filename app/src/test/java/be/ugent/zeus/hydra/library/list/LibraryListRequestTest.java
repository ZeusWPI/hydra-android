package be.ugent.zeus.hydra.library.list;

import be.ugent.zeus.hydra.BuildConfig;
import be.ugent.zeus.hydra.TestApp;
import be.ugent.zeus.hydra.common.network.AbstractJsonSpringRequestTest;
import be.ugent.zeus.hydra.common.network.JsonSpringRequest;
import be.ugent.zeus.hydra.library.list.LibraryList;
import be.ugent.zeus.hydra.library.list.LibraryListRequest;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = TestApp.class)
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