package be.ugent.zeus.hydra.library.details;

import java.io.IOException;
import java.util.List;

import be.ugent.zeus.hydra.common.network.AbstractJsonRequestTest;
import be.ugent.zeus.hydra.library.Library;
import be.ugent.zeus.hydra.library.list.LibraryList;
import com.squareup.moshi.JsonAdapter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author Niko Strijbol
 */
@RunWith(RobolectricTestRunner.class)
public class OpeningHoursRequestTest extends AbstractJsonRequestTest<List<OpeningHours>> {

    @Override
    protected String getRelativePath() {
        return "library_hours.json";
    }

    @Override
    protected OpeningHoursRequest getRequest() {
        try {
            return new OpeningHoursRequest(context, getRandomConstLibrary());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLibraryCode() throws IOException {
        Library library = getRandomConstLibrary();
        OpeningHoursRequest request = getRequest();
        assertThat(request.getAPIUrl(), endsWith("libraries/" + library.getCode() + "/calendar.json"));
    }

    private Library getRandomConstLibrary() throws IOException {
        JsonAdapter<LibraryList> listAdapter = moshi.adapter(LibraryList.class);
        LibraryList list = listAdapter.fromJson(readData(getResourceFile("all_libraries.json")));
        assertNotNull(list);
        assertNotNull(list.getLibraries());
        return list.getLibraries().get(0);
    }
}